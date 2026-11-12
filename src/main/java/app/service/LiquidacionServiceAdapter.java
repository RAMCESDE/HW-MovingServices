package app.service;

import app.domain.Cliente;
import app.domain.EventoServicio;
import app.domain.Pago;
import app.domain.Reserva;
import app.domain.Sesion;
import app.domain.enums.EstadoPago;
import app.domain.enums.EstadoReserva;
import app.domain.enums.MetodoPago;
import app.domain.enums.Rol;
import app.domain.enums.TipoPago;
import app.domain.exceptions.AccesoDenegadoException;
import app.domain.exceptions.DatoInvalidoException;
import app.domain.exceptions.PagoRechazadoException;
import app.domain.exceptions.RecursoNoEncontradoException;
import app.domain.exceptions.ReglaNegocioException;
import app.service.helpers.ControlAcceso;
import app.service.helpers.IdGenerator;
import app.service.helpers.TextoHelper;
import app.service.inputports.LiquidacionServiceInterface;
import app.service.inputports.dto.Factura;
import app.service.outputports.ClienteRepositoryPort;
import app.service.outputports.EventoServicioRepositoryPort;
import app.service.outputports.NotificacionPort;
import app.service.outputports.PagoRepositoryPort;
import app.service.outputports.PasarelaPagoPort;
import app.service.outputports.ReservaRepositoryPort;
import app.service.outputports.TransaccionPort;
import app.service.validators.DatosValidator;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class LiquidacionServiceAdapter implements LiquidacionServiceInterface {

    private final ReservaRepositoryPort reservas;
    private final PagoRepositoryPort pagos;
    private final EventoServicioRepositoryPort eventos;
    private final ClienteRepositoryPort clientes;
    private final PasarelaPagoPort pasarela;
    private final NotificacionPort notificacion;
    private final TransaccionPort transaccion;
    private final Clock reloj;

    public LiquidacionServiceAdapter(ReservaRepositoryPort reservas, PagoRepositoryPort pagos,
                                     EventoServicioRepositoryPort eventos, ClienteRepositoryPort clientes,
                                     PasarelaPagoPort pasarela, NotificacionPort notificacion,
                                     TransaccionPort transaccion, Clock reloj) {
        this.reservas = reservas;
        this.pagos = pagos;
        this.eventos = eventos;
        this.clientes = clientes;
        this.pasarela = pasarela;
        this.notificacion = notificacion;
        this.transaccion = transaccion;
        this.reloj = reloj;
    }

    @Override
    public BigDecimal consultarSaldo(Sesion sesion, String idReserva) {
        ControlAcceso.exigirRol(sesion, Rol.CLIENTE, Rol.CONDUCTOR, Rol.ADMIN);
        Reserva reserva = buscar(idReserva);
        exigirAcceso(sesion, reserva);
        return saldo(reserva);
    }

    @Override
    public Factura pagarSaldoDigital(Sesion sesion, String idReserva, MetodoPago metodo, String tokenPago) {
        Reserva reserva = buscar(idReserva);
        ControlAcceso.exigirPropietario(sesion, reserva.getIdCliente());
        if (DatosValidator.requerido(metodo, "método de pago") == MetodoPago.EFECTIVO) {
            throw new DatoInvalidoException("El pago en efectivo lo registra el conductor al finalizar la entrega");
        }
        exigirEnDestino(reserva);
        BigDecimal saldo = saldo(reserva);
        if (saldo.signum() == 0) {
            return liquidar(sesion, reserva, saldo, metodo, "SIN-SALDO");
        }
        PasarelaPagoPort.ResultadoCobro cobro = pasarela.cobrar(idReserva + "-SALDO", saldo, metodo, tokenPago);
        if (!cobro.aprobado()) {
            throw new PagoRechazadoException("Pago rechazado: " + cobro.motivo() + ". Puede intentar con otro método de pago");
        }
        return liquidar(sesion, reserva, saldo, metodo, cobro.referencia());
    }

    @Override
    public Factura registrarSaldoEfectivo(Sesion sesion, String idReserva) {
        ControlAcceso.exigirRol(sesion, Rol.CONDUCTOR);
        Reserva reserva = buscar(idReserva);
        if (!sesion.idPerfil().equals(reserva.getIdConductor())) {
            throw new AccesoDenegadoException("Este servicio está asignado a otro conductor");
        }
        exigirEnDestino(reserva);
        return liquidar(sesion, reserva, saldo(reserva), MetodoPago.EFECTIVO, "EFECTIVO-" + sesion.idCuenta());
    }

    @Override
    public Factura consultarFactura(Sesion sesion, String idReserva) {
        ControlAcceso.exigirRol(sesion, Rol.CLIENTE, Rol.CONDUCTOR, Rol.ADMIN);
        Reserva reserva = buscar(idReserva);
        exigirAcceso(sesion, reserva);
        if (reserva.getEstado() != EstadoReserva.FINALIZADO) {
            throw new ReglaNegocioException("La factura se emite al finalizar y pagar el servicio");
        }
        return factura(reserva);
    }

    private Factura liquidar(Sesion sesion, Reserva reserva, BigDecimal saldo, MetodoPago metodo, String referencia) {
        LocalDateTime ahora = LocalDateTime.now(reloj);
        Pago pago = new Pago(IdGenerator.nuevo("PAG"), reserva.getIdReserva(), saldo, TipoPago.SALDO, metodo,
                EstadoPago.APROBADO, referencia, ahora);
        reserva.finalizar();
        transaccion.ejecutar(() -> {
            pagos.guardar(pago);
            reservas.guardar(reserva);
            return eventos.guardar(new EventoServicio(IdGenerator.nuevo("EVT"), reserva.getIdReserva(),
                    reserva.getEstado(), ahora, sesion.idCuenta()));
        });
        Factura factura = factura(reserva);
        clientes.buscarPorId(reserva.getIdCliente()).ifPresent(cliente -> notificacion.enviarCorreo(cliente.getEmail(),
                "Factura " + factura.numero(), "Servicio finalizado. Total pagado: " + TextoHelper.pesos(factura.total())
                        + ". Ya puede calificar el servicio."));
        return factura;
    }

    private Factura factura(Reserva reserva) {
        List<Pago> realizados = pagos.listarPorReserva(reserva.getIdReserva()).stream().filter(Pago::aprobado).toList();
        LocalDateTime fecha = realizados.stream().map(Pago::getFecha).max(LocalDateTime::compareTo)
                .orElse(reserva.getFechaServicio());
        Cliente cliente = clientes.buscarPorId(reserva.getIdCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente de la reserva"));
        String nombre = cliente.getNombre() == null ? cliente.getEmail() : cliente.getNombre();
        return new Factura("FAC-" + reserva.getIdReserva(), reserva.getIdReserva(), nombre, reserva.getTotal(),
                realizados, fecha);
    }

    private BigDecimal saldo(Reserva reserva) {
        BigDecimal pagado = pagos.listarPorReserva(reserva.getIdReserva()).stream().filter(Pago::aprobado)
                .map(Pago::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
        return reserva.getTotal().subtract(pagado);
    }

    private void exigirEnDestino(Reserva reserva) {
        if (reserva.getEstado() != EstadoReserva.EN_DESTINO) {
            throw new ReglaNegocioException("El saldo se liquida cuando el conductor marca el servicio En destino");
        }
    }

    private void exigirAcceso(Sesion sesion, Reserva reserva) {
        boolean permitido = switch (sesion.rol()) {
            case ADMIN -> true;
            case CLIENTE -> sesion.idPerfil().equals(reserva.getIdCliente());
            case CONDUCTOR -> sesion.idPerfil().equals(reserva.getIdConductor());
            case AUXILIAR_CARGA -> false;
        };
        if (!permitido) {
            throw new AccesoDenegadoException("No tiene acceso a este servicio");
        }
    }

    private Reserva buscar(String idReserva) {
        return reservas.buscarPorId(idReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la reserva " + idReserva));
    }
}
