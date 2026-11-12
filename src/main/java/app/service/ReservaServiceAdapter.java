package app.service;

import app.domain.Cuenta;
import app.domain.Pago;
import app.domain.Reserva;
import app.domain.Sesion;
import app.domain.SolicitudServicio;
import app.domain.EventoServicio;
import app.domain.enums.EstadoPago;
import app.domain.enums.EstadoReserva;
import app.domain.enums.EstadoSolicitud;
import app.domain.enums.MetodoPago;
import app.domain.enums.Rol;
import app.domain.enums.TipoPago;
import app.domain.exceptions.DatoInvalidoException;
import app.domain.exceptions.PagoRechazadoException;
import app.domain.exceptions.RecursoNoEncontradoException;
import app.domain.exceptions.ReglaNegocioException;
import app.service.helpers.ControlAcceso;
import app.service.helpers.IdGenerator;
import app.service.helpers.TextoHelper;
import app.service.inputports.ReservaServiceInterface;
import app.service.inputports.dto.ResultadoReserva;
import app.service.outputports.CuentaRepositoryPort;
import app.service.outputports.EventoServicioRepositoryPort;
import app.service.outputports.NotificacionPort;
import app.service.outputports.PagoRepositoryPort;
import app.service.outputports.PasarelaPagoPort;
import app.service.outputports.ReservaRepositoryPort;
import app.service.outputports.SolicitudServicioRepositoryPort;
import app.service.outputports.TransaccionPort;
import app.service.validators.DatosValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class ReservaServiceAdapter implements ReservaServiceInterface {

    private static final BigDecimal PORCENTAJE_ANTICIPO = new BigDecimal("0.10");

    private final SolicitudServicioRepositoryPort solicitudes;
    private final ReservaRepositoryPort reservas;
    private final PagoRepositoryPort pagos;
    private final EventoServicioRepositoryPort eventos;
    private final CuentaRepositoryPort cuentas;
    private final PasarelaPagoPort pasarela;
    private final NotificacionPort notificacion;
    private final TransaccionPort transaccion;
    private final Clock reloj;

    public ReservaServiceAdapter(SolicitudServicioRepositoryPort solicitudes, ReservaRepositoryPort reservas,
                                 PagoRepositoryPort pagos, EventoServicioRepositoryPort eventos,
                                 CuentaRepositoryPort cuentas, PasarelaPagoPort pasarela,
                                 NotificacionPort notificacion, TransaccionPort transaccion, Clock reloj) {
        this.solicitudes = solicitudes;
        this.reservas = reservas;
        this.pagos = pagos;
        this.eventos = eventos;
        this.cuentas = cuentas;
        this.pasarela = pasarela;
        this.notificacion = notificacion;
        this.transaccion = transaccion;
        this.reloj = reloj;
    }

    @Override
    public ResultadoReserva reservar(Sesion sesion, String idSolicitud, LocalDateTime fechaServicio, MetodoPago metodo,
                                     BigDecimal anticipo, String tokenPago) {
        ControlAcceso.exigirRol(sesion, Rol.CLIENTE);
        SolicitudServicio solicitud = solicitudes.buscarPorId(idSolicitud)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la cotización " + idSolicitud));
        ControlAcceso.exigirPropietario(sesion, solicitud.getIdCliente());
        if (solicitud.getEstado() != EstadoSolicitud.ACEPTADA) {
            throw new ReglaNegocioException("Primero debe aceptar la cotización para poder reservar");
        }
        DatosValidator.requerido(metodo, "método de pago");
        LocalDateTime ahora = LocalDateTime.now(reloj);
        if (fechaServicio == null || !fechaServicio.isAfter(ahora)) {
            throw new DatoInvalidoException("La fecha y hora del servicio debe ser futura");
        }
        BigDecimal total = solicitud.getCotizacion().total();
        BigDecimal minimo = total.multiply(PORCENTAJE_ANTICIPO).setScale(0, RoundingMode.CEILING);
        BigDecimal monto = anticipo == null ? minimo : anticipo;
        if (monto.compareTo(minimo) < 0 || monto.compareTo(total) > 0) {
            throw new DatoInvalidoException("El anticipo debe estar entre " + TextoHelper.pesos(minimo) + " (10%) y " + TextoHelper.pesos(total));
        }

        String idReserva = IdGenerator.nuevo("RES");
        Pago pago = new Pago(IdGenerator.nuevo("PAG"), idReserva, monto, TipoPago.ANTICIPO, metodo, EstadoPago.PENDIENTE,
                null, ahora);
        Reserva reserva = new Reserva();
        reserva.setIdReserva(idReserva);
        reserva.setIdSolicitud(idSolicitud);
        reserva.setIdCliente(sesion.idPerfil());
        reserva.setFechaReserva(ahora);
        reserva.setFechaServicio(fechaServicio);
        reserva.setDuracionMinutos(solicitud.getCotizacion().duracionMinutos());
        reserva.setTotal(total);
        reserva.setEstado(EstadoReserva.PENDIENTE_ANTICIPO);

        if (metodo != MetodoPago.EFECTIVO) {
            PasarelaPagoPort.ResultadoCobro cobro = pasarela.cobrar(idReserva, monto, metodo, tokenPago);
            if (!cobro.aprobado()) {
                // Sin cobro no hay reserva: la cotización sigue aceptada y se puede reintentar.
                throw new PagoRechazadoException("Pago rechazado: " + cobro.motivo()
                        + ". Puede intentar con otro método de pago");
            }
            pago.aprobar(cobro.referencia(), ahora);
            reserva.confirmarAnticipo();
        }

        solicitud.marcarReservada();
        transaccion.ejecutar(() -> {
            solicitudes.guardar(solicitud);
            reservas.guardar(reserva);
            pagos.guardar(pago);
            return eventos.guardar(evento(reserva, ahora, sesion));
        });

        String comprobante = comprobante(reserva, pago);
        notificacion.enviarCorreo(sesion.email(), "Comprobante provisional de reserva " + idReserva, comprobante);
        return new ResultadoReserva(reserva, pago, comprobante);
    }

    @Override
    public ResultadoReserva registrarAnticipoEfectivo(Sesion sesion, String idReserva) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        Reserva reserva = reservas.buscarPorId(idReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la reserva " + idReserva));
        reserva.confirmarAnticipo();
        Pago pago = pagos.listarPorReserva(idReserva).stream()
                .filter(p -> p.getTipoPago() == TipoPago.ANTICIPO).findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("La reserva no tiene anticipo registrado"));
        LocalDateTime ahora = LocalDateTime.now(reloj);
        pago.aprobar("EFECTIVO-" + sesion.idCuenta(), ahora);
        transaccion.ejecutar(() -> {
            reservas.guardar(reserva);
            pagos.guardar(pago);
            return eventos.guardar(evento(reserva, ahora, sesion));
        });

        String comprobante = comprobante(reserva, pago);
        cuentas.buscarPorPerfil(reserva.getIdCliente()).map(Cuenta::getEmail).ifPresent(correo ->
                notificacion.enviarCorreo(correo, "Comprobante provisional de reserva " + idReserva, comprobante));
        return new ResultadoReserva(reserva, pago, comprobante);
    }

    @Override
    public List<Reserva> listarMisReservas(Sesion sesion) {
        ControlAcceso.exigirRol(sesion, Rol.CLIENTE);
        return reservas.listarPorCliente(sesion.idPerfil());
    }

    @Override
    public List<Reserva> listarAnticiposPendientes(Sesion sesion) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        return reservas.listarPorEstado(EstadoReserva.PENDIENTE_ANTICIPO);
    }

    private EventoServicio evento(Reserva reserva, LocalDateTime fecha, Sesion sesion) {
        return new EventoServicio(IdGenerator.nuevo("EVT"), reserva.getIdReserva(), reserva.getEstado(), fecha,
                sesion.idCuenta());
    }

    private String comprobante(Reserva reserva, Pago pago) {
        String pagoTexto = pago.aprobado()
                ? "Anticipo pagado: " + TextoHelper.pesos(pago.getMonto()) + " por " + pago.getMetodoPago().getEtiqueta()
                : "Anticipo por pagar en efectivo: " + TextoHelper.pesos(pago.getMonto());
        return "COMPROBANTE PROVISIONAL " + reserva.getIdReserva()
                + "\nEstado: " + reserva.getEstado().getEtiqueta()
                + "\nFecha del servicio: " + TextoHelper.fecha(reserva.getFechaServicio())
                + "\nTotal cotizado: " + TextoHelper.pesos(reserva.getTotal())
                + "\n" + pagoTexto
                + "\nSaldo restante: " + TextoHelper.pesos(reserva.getTotal().subtract(pago.getMonto()));
    }
}
