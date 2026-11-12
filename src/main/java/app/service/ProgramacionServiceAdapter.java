package app.service;

import app.domain.Empleado;
import app.domain.EventoServicio;
import app.domain.Reserva;
import app.domain.RestriccionPicoPlaca;
import app.domain.Sesion;
import app.domain.SolicitudServicio;
import app.domain.Vehiculo;
import app.domain.enums.CargoEmpleado;
import app.domain.enums.EstadoReserva;
import app.domain.enums.Rol;
import app.domain.exceptions.DatoInvalidoException;
import app.domain.exceptions.RecursoNoEncontradoException;
import app.domain.exceptions.ReglaNegocioException;
import app.service.helpers.ControlAcceso;
import app.service.helpers.IdGenerator;
import app.service.helpers.TextoHelper;
import app.service.inputports.ProgramacionServiceInterface;
import app.service.inputports.dto.ItemAgenda;
import app.service.inputports.dto.ProgramacionServicio;
import app.service.inputports.dto.RecursosDisponibles;
import app.service.outputports.CuentaRepositoryPort;
import app.service.outputports.EmpleadoRepositoryPort;
import app.service.outputports.EventoServicioRepositoryPort;
import app.service.outputports.NotificacionPort;
import app.service.outputports.ReservaRepositoryPort;
import app.service.outputports.RestriccionPicoPlacaRepositoryPort;
import app.service.outputports.SolicitudServicioRepositoryPort;
import app.service.outputports.TransaccionPort;
import app.service.outputports.VehiculoRepositoryPort;
import app.service.validators.DatosValidator;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class ProgramacionServiceAdapter implements ProgramacionServiceInterface {

    private final ReservaRepositoryPort reservas;
    private final SolicitudServicioRepositoryPort solicitudes;
    private final VehiculoRepositoryPort vehiculos;
    private final EmpleadoRepositoryPort empleados;
    private final RestriccionPicoPlacaRepositoryPort restricciones;
    private final EventoServicioRepositoryPort eventos;
    private final CuentaRepositoryPort cuentas;
    private final NotificacionPort notificacion;
    private final TransaccionPort transaccion;
    private final Clock reloj;

    public ProgramacionServiceAdapter(ReservaRepositoryPort reservas, SolicitudServicioRepositoryPort solicitudes,
                                      VehiculoRepositoryPort vehiculos, EmpleadoRepositoryPort empleados,
                                      RestriccionPicoPlacaRepositoryPort restricciones,
                                      EventoServicioRepositoryPort eventos, CuentaRepositoryPort cuentas,
                                      NotificacionPort notificacion, TransaccionPort transaccion, Clock reloj) {
        this.reservas = reservas;
        this.solicitudes = solicitudes;
        this.vehiculos = vehiculos;
        this.empleados = empleados;
        this.restricciones = restricciones;
        this.eventos = eventos;
        this.cuentas = cuentas;
        this.notificacion = notificacion;
        this.transaccion = transaccion;
        this.reloj = reloj;
    }

    @Override
    public List<Reserva> listarPorProgramar(Sesion sesion) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        return reservas.listarPorEstado(EstadoReserva.RESERVADO);
    }

    @Override
    public RecursosDisponibles consultarRecursos(Sesion sesion, String idReserva, LocalDateTime fechaServicio) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        Contexto contexto = contexto(idReserva, fechaServicio);
        List<Vehiculo> flota = vehiculos.listar().stream()
                .filter(v -> problemaVehiculo(v, contexto).isEmpty()).toList();
        List<Empleado> personal = empleados.listar();
        List<Empleado> conductores = personal.stream().filter(e -> problemaConductor(e, contexto).isEmpty()).toList();
        List<Empleado> auxiliares = personal.stream().filter(e -> problemaAuxiliar(e, contexto).isEmpty()).toList();
        return new RecursosDisponibles(flota, conductores, auxiliares, contexto.solicitud().getAyudantes());
    }

    @Override
    public Reserva programar(Sesion sesion, ProgramacionServicio datos) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        Contexto contexto = contexto(datos.idReserva(), datos.fechaServicio());

        String placa = DatosValidator.placa(datos.placaVehiculo());
        Vehiculo vehiculo = vehiculos.buscarPorPlaca(placa)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el vehículo " + placa));
        problemaVehiculo(vehiculo, contexto).ifPresent(ProgramacionServiceAdapter::rechazar);

        Empleado conductor = buscarEmpleado(datos.idConductor());
        problemaConductor(conductor, contexto).ifPresent(ProgramacionServiceAdapter::rechazar);

        List<String> idsAuxiliares = datos.idsAuxiliares() == null ? List.of() : datos.idsAuxiliares();
        if (new HashSet<>(idsAuxiliares).size() != idsAuxiliares.size()) {
            throw new DatoInvalidoException("Hay auxiliares repetidos en la asignación");
        }
        if (idsAuxiliares.size() != contexto.solicitud().getAyudantes()) {
            throw new ReglaNegocioException("El servicio requiere " + contexto.solicitud().getAyudantes()
                    + " auxiliares y se asignaron " + idsAuxiliares.size());
        }
        List<Empleado> auxiliares = new ArrayList<>();
        for (String id : idsAuxiliares) {
            Empleado auxiliar = buscarEmpleado(id);
            problemaAuxiliar(auxiliar, contexto).ifPresent(ProgramacionServiceAdapter::rechazar);
            auxiliares.add(auxiliar);
        }

        Reserva reserva = contexto.reserva();
        reserva.programar(datos.fechaServicio(), placa, conductor.getIdEmpleado(), idsAuxiliares);
        transaccion.ejecutar(() -> {
            reservas.guardar(reserva);
            return eventos.guardar(new EventoServicio(IdGenerator.nuevo("EVT"), reserva.getIdReserva(),
                    reserva.getEstado(), LocalDateTime.now(reloj), sesion.idCuenta()));
        });
        notificar(reserva, contexto.solicitud(), vehiculo, conductor, auxiliares);
        return reserva;
    }

    @Override
    public List<ItemAgenda> consultarAgenda(Sesion sesion, LocalDate desde, LocalDate hasta) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        DatosValidator.requerido(desde, "fecha inicial");
        DatosValidator.requerido(hasta, "fecha final");
        if (hasta.isBefore(desde)) {
            throw new DatoInvalidoException("La fecha final no puede ser anterior a la inicial");
        }
        return reservas.listarEntre(desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay()).stream()
                .filter(r -> r.getEstado().ocupaRecursos() || r.getEstado() == EstadoReserva.FINALIZADO)
                .map(this::itemAgenda)
                .sorted(Comparator.comparing(ItemAgenda::inicio).thenComparing(ItemAgenda::placa))
                .toList();
    }

    private ItemAgenda itemAgenda(Reserva reserva) {
        SolicitudServicio solicitud = solicitudes.buscarPorId(reserva.getIdSolicitud()).orElseThrow();
        return new ItemAgenda(reserva.getIdReserva(), reserva.getFechaServicio(), reserva.getFechaFin(),
                reserva.getEstado(), reserva.getPlacaVehiculo(), nombre(reserva.getIdConductor()),
                reserva.getIdsAuxiliares().stream().map(this::nombre).toList(),
                solicitud.getOrigen().municipio().getEtiqueta(), solicitud.getDestino().municipio().getEtiqueta());
    }

    private String nombre(String idEmpleado) {
        return empleados.buscarPorId(idEmpleado).map(Empleado::getNombre).orElse(idEmpleado);
    }

    private Contexto contexto(String idReserva, LocalDateTime fechaServicio) {
        Reserva reserva = reservas.buscarPorId(idReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la reserva " + idReserva));
        if (reserva.getEstado() != EstadoReserva.RESERVADO) {
            throw new ReglaNegocioException("Solo se programan servicios en estado Reservado; la reserva "
                    + idReserva + " está en \"" + reserva.getEstado().getEtiqueta() + "\"");
        }
        LocalDateTime inicio = fechaServicio == null ? reserva.getFechaServicio() : fechaServicio;
        LocalDateTime ahora = LocalDateTime.now(reloj);
        if (!inicio.isAfter(ahora)) {
            throw new DatoInvalidoException("La fecha del servicio debe ser futura");
        }
        LocalDateTime fin = inicio.plusMinutes(reserva.getDuracionMinutos());
        SolicitudServicio solicitud = solicitudes.buscarPorId(reserva.getIdSolicitud())
                .orElseThrow(() -> new RecursoNoEncontradoException("La reserva no tiene solicitud asociada"));
        List<Reserva> ocupadas = reservas.listarEntre(inicio, fin).stream()
                .filter(r -> r.getEstado().ocupaRecursos() && !r.getIdReserva().equals(idReserva)).toList();
        return new Contexto(reserva, solicitud, inicio, fin, ahora.toLocalDate(), ocupadas, restricciones.listar());
    }

    private Optional<String> problemaVehiculo(Vehiculo vehiculo, Contexto c) {
        String placa = vehiculo.getPlaca();
        vehiculo.sincronizarEstadoConDocumentos(c.hoy());
        if (!vehiculo.verificarDisponibilidad()) {
            vehiculos.guardar(vehiculo);
            return Optional.of("El vehículo " + placa + " está " + vehiculo.getEstado().getEtiqueta());
        }
        if (!vehiculo.documentosVigentesEn(c.inicio().toLocalDate()) || !vehiculo.documentosVigentesEn(c.fin().toLocalDate())) {
            return Optional.of("Los documentos del vehículo " + placa + " vencen antes del servicio");
        }
        if (c.solicitud().getVolumenM3() > vehiculo.getCapacidadVolumenM3()) {
            return Optional.of("El vehículo " + placa + " (" + vehiculo.getCapacidadVolumenM3()
                    + " m³) no tiene capacidad para " + c.solicitud().getVolumenM3() + " m³");
        }
        if (c.restricciones().stream().anyMatch(r -> r.restringe(vehiculo, c.inicio(), c.fin()))) {
            return Optional.of("Restricción de Pico y Placa: el vehículo " + placa + " no puede circular en ese horario");
        }
        if (c.ocupadas().stream().anyMatch(r -> placa.equals(r.getPlacaVehiculo()))) {
            return Optional.of("El vehículo " + placa + " ya tiene un servicio en ese horario");
        }
        return Optional.empty();
    }

    private Optional<String> problemaConductor(Empleado empleado, Contexto c) {
        if (empleado.getCargo() != CargoEmpleado.CONDUCTOR) {
            return Optional.of(empleado.getNombre() + " no tiene el cargo de conductor");
        }
        if (!empleado.licenciaVigenteEn(c.inicio().toLocalDate())) {
            return Optional.of("La licencia de " + empleado.getNombre() + " no está vigente para la fecha del servicio");
        }
        return problemaPersonal(empleado, c);
    }

    private Optional<String> problemaAuxiliar(Empleado empleado, Contexto c) {
        if (empleado.getCargo() != CargoEmpleado.AUXILIAR_CARGA) {
            return Optional.of(empleado.getNombre() + " no tiene el cargo de auxiliar de carga");
        }
        return problemaPersonal(empleado, c);
    }

    private Optional<String> problemaPersonal(Empleado empleado, Contexto c) {
        if (!empleado.esAsignable()) {
            return Optional.of(empleado.getNombre() + " está " + empleado.getEstado().getEtiqueta());
        }
        if (c.ocupadas().stream().anyMatch(r -> r.participa(empleado.getIdEmpleado()))) {
            return Optional.of(empleado.getNombre() + " ya tiene un servicio en ese horario");
        }
        return Optional.empty();
    }

    private Empleado buscarEmpleado(String id) {
        return empleados.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el empleado " + id));
    }

    private static void rechazar(String motivo) {
        throw new ReglaNegocioException(motivo);
    }

    private void notificar(Reserva reserva, SolicitudServicio solicitud, Vehiculo vehiculo, Empleado conductor,
                           List<Empleado> auxiliares) {
        String ruta = solicitud.getOrigen().direccion() + " (" + solicitud.getOrigen().municipio().getEtiqueta() + ") → "
                + solicitud.getDestino().direccion() + " (" + solicitud.getDestino().municipio().getEtiqueta() + ")";
        String equipo = "Conductor: " + conductor.getNombre() + " (documento " + conductor.getCedula() + ")"
                + auxiliares.stream().map(a -> "\nAuxiliar: " + a.getNombre() + " (documento " + a.getCedula() + ")")
                .reduce("", String::concat);
        cuentas.buscarPorPerfil(reserva.getIdCliente()).ifPresent(cuenta -> notificacion.enviarCorreo(cuenta.getEmail(),
                "Servicio confirmado " + reserva.getIdReserva(),
                "Fecha: " + TextoHelper.fecha(reserva.getFechaServicio()) + "\nVehículo: " + vehiculo.getPlaca() + " - "
                        + vehiculo.getModelo() + "\n" + equipo));
        List<Empleado> tripulacion = new ArrayList<>(auxiliares);
        tripulacion.add(conductor);
        for (Empleado integrante : tripulacion) {
            notificacion.enviarCorreo(integrante.getEmail(), "Nuevo servicio asignado " + reserva.getIdReserva(),
                    "Fecha: " + TextoHelper.fecha(reserva.getFechaServicio()) + "\nVehículo: " + vehiculo.getPlaca() + "\nRuta: " + ruta);
        }
    }

    private record Contexto(Reserva reserva, SolicitudServicio solicitud, LocalDateTime inicio, LocalDateTime fin,
                            LocalDate hoy, List<Reserva> ocupadas, List<RestriccionPicoPlaca> restricciones) {
    }
}
