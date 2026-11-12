package app.service;

import app.domain.Empleado;
import app.domain.EventoServicio;
import app.domain.Reserva;
import app.domain.Sesion;
import app.domain.SolicitudServicio;
import app.domain.enums.Rol;
import app.domain.exceptions.AccesoDenegadoException;
import app.domain.exceptions.RecursoNoEncontradoException;
import app.service.helpers.ControlAcceso;
import app.service.helpers.IdGenerator;
import app.service.inputports.SeguimientoServiceInterface;
import app.service.inputports.dto.EstadoServicio;
import app.service.inputports.dto.ServicioAsignado;
import app.service.outputports.CuentaRepositoryPort;
import app.service.outputports.EmpleadoRepositoryPort;
import app.service.outputports.EventoServicioRepositoryPort;
import app.service.outputports.NotificacionPort;
import app.service.outputports.ReservaRepositoryPort;
import app.service.outputports.SolicitudServicioRepositoryPort;
import app.service.outputports.TransaccionPort;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class SeguimientoServiceAdapter implements SeguimientoServiceInterface {

    private final ReservaRepositoryPort reservas;
    private final SolicitudServicioRepositoryPort solicitudes;
    private final EventoServicioRepositoryPort eventos;
    private final EmpleadoRepositoryPort empleados;
    private final CuentaRepositoryPort cuentas;
    private final NotificacionPort notificacion;
    private final TransaccionPort transaccion;
    private final Clock reloj;

    public SeguimientoServiceAdapter(ReservaRepositoryPort reservas, SolicitudServicioRepositoryPort solicitudes,
                                     EventoServicioRepositoryPort eventos, EmpleadoRepositoryPort empleados,
                                     CuentaRepositoryPort cuentas, NotificacionPort notificacion,
                                     TransaccionPort transaccion, Clock reloj) {
        this.reservas = reservas;
        this.solicitudes = solicitudes;
        this.eventos = eventos;
        this.empleados = empleados;
        this.cuentas = cuentas;
        this.notificacion = notificacion;
        this.transaccion = transaccion;
        this.reloj = reloj;
    }

    @Override
    public EstadoServicio avanzarHito(Sesion sesion, String idReserva) {
        ControlAcceso.exigirRol(sesion, Rol.CONDUCTOR);
        Reserva reserva = buscar(idReserva);
        if (!sesion.idPerfil().equals(reserva.getIdConductor())) {
            throw new AccesoDenegadoException("Este servicio está asignado a otro conductor");
        }
        reserva.avanzarHito();
        transaccion.ejecutar(() -> {
            reservas.guardar(reserva);
            return eventos.guardar(new EventoServicio(IdGenerator.nuevo("EVT"), idReserva, reserva.getEstado(),
                    LocalDateTime.now(reloj), sesion.idCuenta()));
        });
        cuentas.buscarPorPerfil(reserva.getIdCliente()).ifPresent(cuenta -> notificacion.enviarCorreo(cuenta.getEmail(),
                "Su mudanza " + idReserva + ": " + reserva.getEstado().getEtapaCliente(),
                "Estado actual del servicio: " + reserva.getEstado().getEtiqueta()));
        return estado(reserva);
    }

    @Override
    public EstadoServicio consultarEstado(Sesion sesion, String idReserva) {
        ControlAcceso.exigirRol(sesion, Rol.CLIENTE, Rol.ADMIN, Rol.CONDUCTOR, Rol.AUXILIAR_CARGA);
        Reserva reserva = buscar(idReserva);
        boolean permitido = switch (sesion.rol()) {
            case ADMIN -> true;
            case CLIENTE -> sesion.idPerfil().equals(reserva.getIdCliente());
            case CONDUCTOR, AUXILIAR_CARGA -> reserva.participa(sesion.idPerfil());
        };
        if (!permitido) {
            throw new AccesoDenegadoException("No tiene acceso a este servicio");
        }
        return estado(reserva);
    }

    @Override
    public List<ServicioAsignado> listarMisServicios(Sesion sesion) {
        ControlAcceso.exigirRol(sesion, Rol.CONDUCTOR, Rol.AUXILIAR_CARGA);
        return reservas.listarPorEmpleado(sesion.idPerfil()).stream().map(reserva -> {
            SolicitudServicio solicitud = solicitudes.buscarPorId(reserva.getIdSolicitud()).orElseThrow();
            return new ServicioAsignado(reserva.getIdReserva(), reserva.getFechaServicio(), reserva.getEstado(),
                    reserva.getPlacaVehiculo(),
                    solicitud.getOrigen().direccion() + ", " + solicitud.getOrigen().municipio().getEtiqueta(),
                    solicitud.getDestino().direccion() + ", " + solicitud.getDestino().municipio().getEtiqueta());
        }).toList();
    }

    private Reserva buscar(String idReserva) {
        return reservas.buscarPorId(idReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la reserva " + idReserva));
    }

    private EstadoServicio estado(Reserva reserva) {
        String conductor = reserva.getIdConductor() == null ? null
                : empleados.buscarPorId(reserva.getIdConductor()).map(Empleado::getNombre).orElse(null);
        return new EstadoServicio(reserva.getIdReserva(), reserva.getEstado().getEtapaCliente(), reserva.getEstado(),
                reserva.getFechaServicio(), reserva.getPlacaVehiculo(), conductor,
                eventos.listarPorReserva(reserva.getIdReserva()));
    }
}
