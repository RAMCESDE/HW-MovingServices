package app.service;

import app.domain.Empleado;
import app.domain.Reserva;
import app.domain.Sesion;
import app.domain.Vehiculo;
import app.domain.enums.EstadoEmpleado;
import app.domain.enums.EstadoReserva;
import app.domain.enums.MetodoPago;
import app.domain.enums.CargoEmpleado;
import app.domain.exceptions.AccesoDenegadoException;
import app.domain.exceptions.DatoInvalidoException;
import app.domain.exceptions.PagoRechazadoException;
import app.domain.exceptions.ReglaNegocioException;
import app.service.inputports.dto.NuevaRestriccionPicoPlaca;
import app.service.inputports.dto.ProgramacionServicio;
import app.service.inputports.dto.RecursosDisponibles;
import app.service.inputports.dto.RegistroVehiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Épica 4: US-14 a US-17. */
class OperacionServiceTest {

    private final Entorno e = new Entorno();
    private final LocalDateTime fecha = Entorno.FECHA_SERVICIO;
    private Sesion cliente;
    private Reserva reserva;
    private Empleado conductor;
    private Empleado auxiliar;

    @BeforeEach
    void preparar() {
        cliente = e.cliente();
        reserva = e.reservaPagada(cliente, 1, 5);
        conductor = e.conductor();
        auxiliar = e.auxiliar();
        e.vehiculo("ABC123", 12);
    }

    private ProgramacionServicio programacion(String placa) {
        return new ProgramacionServicio(reserva.getIdReserva(), fecha, placa, conductor.getIdEmpleado(),
                List.of(auxiliar.getIdEmpleado()));
    }

    private void rechazaCon(String texto, ProgramacionServicio datos) {
        var error = assertThrows(ReglaNegocioException.class, () -> e.s.programacion().programar(e.admin, datos));
        assertTrue(error.getMessage().contains(texto), error.getMessage());
    }

    @Test
    void us14_programaConfirmaYNotificaAClienteYEquipo() {
        Reserva programada = e.s.programacion().programar(e.admin, programacion("ABC123"));

        assertEquals(EstadoReserva.CONFIRMADO, programada.getEstado());
        Reserva guardada = e.repos.reservas().buscarPorId(reserva.getIdReserva()).orElseThrow();
        assertEquals("ABC123", guardada.getPlacaVehiculo());
        assertEquals(List.of(auxiliar.getIdEmpleado()), guardada.getIdsAuxiliares());
        assertTrue(e.correos.stream().anyMatch(c -> c.destinatario().equals(cliente.email()) && c.mensaje().contains("ABC123")
                && c.mensaje().contains(conductor.getCedula())));
        assertTrue(e.correos.stream().anyMatch(c -> c.destinatario().equals(conductor.getEmail()) && c.mensaje().contains("Calle 10")));
        assertTrue(e.correos.stream().anyMatch(c -> c.destinatario().equals(auxiliar.getEmail())));
    }

    @Test
    void us14_bloqueaPicoYPlacaIndicandoLaRestriccion() {
        e.s.picoPlaca().crearRestriccion(e.admin, new NuevaRestriccionPicoPlaca(fecha.getDayOfWeek(), Set.of(3),
                LocalTime.of(5, 0), LocalTime.of(20, 0), LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)));

        rechazaCon("Pico y Placa", programacion("ABC123"));
    }

    @Test
    void us14_bloqueaDocumentosVencidosOQueVencenAntesDelServicio() {
        e.s.flota().registrarVehiculo(e.admin, new RegistroVehiculo("VEN111", app.domain.enums.TipoVehiculo.LUV, "2020", 2000, 12,
                LocalDate.of(2026, 10, 1), LocalDate.of(2027, 1, 1)));
        e.s.flota().registrarVehiculo(e.admin, new RegistroVehiculo("VEN222", app.domain.enums.TipoVehiculo.LUV, "2020", 2000, 12,
                LocalDate.of(2026, 11, 5), LocalDate.of(2027, 1, 1)));

        rechazaCon("Inhabilitado", programacion("VEN111"));
        rechazaCon("vencen antes", programacion("VEN222"));
    }

    @Test
    void us14_bloqueaVehiculoSinCapacidad() {
        e.vehiculo("MIN111", 2);

        rechazaCon("capacidad", programacion("MIN111"));
    }

    @Test
    void us14_bloqueaLicenciaVencidaYEmpleadosInactivos() {
        Empleado sinLicencia = e.empleado(CargoEmpleado.CONDUCTOR, "C2", LocalDate.of(2026, 11, 5));
        rechazaCon("licencia", new ProgramacionServicio(reserva.getIdReserva(), fecha, "ABC123", sinLicencia.getIdEmpleado(),
                List.of(auxiliar.getIdEmpleado())));

        e.s.empleado().cambiarEstado(e.admin, auxiliar.getIdEmpleado(), EstadoEmpleado.INACTIVO);
        rechazaCon("Inactivo", programacion("ABC123"));
    }

    @Test
    void us10_elEmpleadoInactivoNoApareceEntreLosRecursos() {
        e.s.empleado().cambiarEstado(e.admin, auxiliar.getIdEmpleado(), EstadoEmpleado.INACTIVO);

        RecursosDisponibles recursos = e.s.programacion().consultarRecursos(e.admin, reserva.getIdReserva(), fecha);

        assertTrue(recursos.auxiliares().isEmpty());
        assertEquals(1, recursos.conductores().size());
        assertEquals(1, recursos.auxiliaresRequeridos());
    }

    @Test
    void us14_exigeLaCantidadDeAuxiliaresSolicitadaYCargosCorrectos() {
        rechazaCon("requiere 1", new ProgramacionServicio(reserva.getIdReserva(), fecha, "ABC123", conductor.getIdEmpleado(), List.of()));
        rechazaCon("conductor", new ProgramacionServicio(reserva.getIdReserva(), fecha, "ABC123", auxiliar.getIdEmpleado(),
                List.of(auxiliar.getIdEmpleado())));
        rechazaCon("auxiliar", new ProgramacionServicio(reserva.getIdReserva(), fecha, "ABC123", conductor.getIdEmpleado(),
                List.of(conductor.getIdEmpleado())));
    }

    @Test
    void us14_soloProgramaServiciosReservados() {
        e.s.programacion().programar(e.admin, programacion("ABC123"));

        rechazaCon("Reservado", programacion("ABC123"));
    }

    @Test
    void us14_noPermiteFechaPasada() {
        assertThrows(DatoInvalidoException.class, () -> e.s.programacion().programar(e.admin,
                new ProgramacionServicio(reserva.getIdReserva(), Entorno.AHORA, "ABC123", conductor.getIdEmpleado(),
                        List.of(auxiliar.getIdEmpleado()))));
    }

    @Test
    void us15_bloqueaTraslapesDeVehiculoYPersonalPeroPermiteOtroHorario() {
        e.s.programacion().programar(e.admin, programacion("ABC123"));
        Reserva otra = e.reservaPagada(e.cliente(), 1, 5);
        Empleado conductor2 = e.conductor();
        Empleado auxiliar2 = e.auxiliar();
        e.vehiculo("DEF456", 12);

        rechazaCon("vehículo", new ProgramacionServicio(otra.getIdReserva(), fecha.plusHours(1), "ABC123",
                conductor2.getIdEmpleado(), List.of(auxiliar2.getIdEmpleado())));
        rechazaCon("ya tiene un servicio", new ProgramacionServicio(otra.getIdReserva(), fecha.plusHours(1), "DEF456",
                conductor.getIdEmpleado(), List.of(auxiliar2.getIdEmpleado())));
        rechazaCon("ya tiene un servicio", new ProgramacionServicio(otra.getIdReserva(), fecha.plusHours(1), "DEF456",
                conductor2.getIdEmpleado(), List.of(auxiliar.getIdEmpleado())));

        Reserva programada = e.s.programacion().programar(e.admin, new ProgramacionServicio(otra.getIdReserva(),
                fecha.plusDays(1), "ABC123", conductor.getIdEmpleado(), List.of(auxiliar.getIdEmpleado())));
        assertEquals(EstadoReserva.CONFIRMADO, programada.getEstado());
    }

    @Test
    void us15_laAgendaMuestraLosServiciosDelRangoConSusRecursos() {
        e.s.programacion().programar(e.admin, programacion("ABC123"));

        var agenda = e.s.programacion().consultarAgenda(e.admin, fecha.toLocalDate(), fecha.toLocalDate());

        assertEquals(1, agenda.size());
        assertEquals("ABC123", agenda.get(0).placa());
        assertEquals(conductor.getNombre(), agenda.get(0).conductor());
        assertEquals(List.of(auxiliar.getNombre()), agenda.get(0).auxiliares());
        assertTrue(e.s.programacion().consultarAgenda(e.admin, fecha.toLocalDate().plusDays(1), fecha.toLocalDate().plusDays(2)).isEmpty());
    }

    @Test
    void us16_elConductorActualizaHitosYElClienteVeLasTresEtapas() {
        e.s.programacion().programar(e.admin, programacion("ABC123"));
        Sesion sesionConductor = e.sesionDe(conductor);
        String id = reserva.getIdReserva();

        assertEquals("Pendiente", e.s.seguimiento().consultarEstado(cliente, id).etapa());
        e.s.seguimiento().avanzarHito(sesionConductor, id);
        assertEquals("En Trayecto", e.s.seguimiento().consultarEstado(cliente, id).etapa());
        assertEquals(EstadoReserva.EN_DESPLAZAMIENTO, e.s.seguimiento().consultarEstado(cliente, id).detalle());
        assertEquals(cliente.email(), e.correos.get(e.correos.size() - 1).destinatario());
        e.s.seguimiento().avanzarHito(sesionConductor, id);
        e.s.seguimiento().avanzarHito(sesionConductor, id);
        assertEquals(EstadoReserva.EN_DESTINO, e.s.seguimiento().consultarEstado(cliente, id).detalle());
        assertEquals(5, e.s.seguimiento().consultarEstado(cliente, id).historial().size());

        e.s.liquidacion().registrarSaldoEfectivo(sesionConductor, id);
        assertEquals("Finalizada", e.s.seguimiento().consultarEstado(cliente, id).etapa());
    }

    @Test
    void us16_soloElConductorAsignadoAvanzaYSoloElDuenoConsulta() {
        e.s.programacion().programar(e.admin, programacion("ABC123"));
        Sesion otroConductor = e.sesionDe(e.conductor());
        String id = reserva.getIdReserva();

        assertThrows(AccesoDenegadoException.class, () -> e.s.seguimiento().avanzarHito(otroConductor, id));
        assertThrows(AccesoDenegadoException.class, () -> e.s.seguimiento().avanzarHito(cliente, id));
        assertThrows(AccesoDenegadoException.class, () -> e.s.seguimiento().consultarEstado(e.cliente(), id));
        assertEquals(1, e.s.seguimiento().listarMisServicios(e.sesionDe(auxiliar)).size());
    }

    @Test
    void us17_elClientePagaElSaldoDigitalYRecibeRecibo() {
        var p = e.servicioEnDestino();
        BigDecimal total = e.repos.reservas().buscarPorId(p.idReserva()).orElseThrow().getTotal();
        BigDecimal saldo = e.s.liquidacion().consultarSaldo(p.cliente(), p.idReserva());

        var factura = e.s.liquidacion().pagarSaldoDigital(p.cliente(), p.idReserva(), MetodoPago.TARJETA, "tok_aprobado");

        assertTrue(saldo.compareTo(total) < 0);
        assertEquals(2, factura.pagos().size());
        BigDecimal pagado = factura.pagos().stream().map(app.domain.Pago::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, total.compareTo(pagado));
        assertEquals(0, BigDecimal.ZERO.compareTo(e.s.liquidacion().consultarSaldo(p.cliente(), p.idReserva())));
        assertEquals(factura.numero(), e.s.liquidacion().consultarFactura(p.cliente(), p.idReserva()).numero());
        assertEquals(EstadoReserva.FINALIZADO, e.repos.reservas().buscarPorId(p.idReserva()).orElseThrow().getEstado());
    }

    @Test
    void us17_pagoRechazadoNoFinalizaYElClienteNoRegistraEfectivo() {
        var p = e.servicioEnDestino();

        assertThrows(PagoRechazadoException.class,
                () -> e.s.liquidacion().pagarSaldoDigital(p.cliente(), p.idReserva(), MetodoPago.TARJETA, "tok_rechazado"));
        assertThrows(DatoInvalidoException.class,
                () -> e.s.liquidacion().pagarSaldoDigital(p.cliente(), p.idReserva(), MetodoPago.EFECTIVO, null));
        assertEquals(EstadoReserva.EN_DESTINO, e.repos.reservas().buscarPorId(p.idReserva()).orElseThrow().getEstado());
    }

    @Test
    void us17_noSeLiquidaAntesDeLlegarADestino() {
        var p = e.servicioProgramado();

        assertThrows(ReglaNegocioException.class, () -> e.s.liquidacion().registrarSaldoEfectivo(p.conductorSesion(), p.idReserva()));
        assertThrows(ReglaNegocioException.class, () -> e.s.liquidacion().consultarFactura(p.cliente(), p.idReserva()));
    }

    @Test
    void us17_calificaUnaVezYActualizaElPerfilDelPersonalYLasMetricas() {
        var p = e.servicioEnDestino();
        assertThrows(ReglaNegocioException.class, () -> e.s.evaluacion().calificar(p.cliente(), p.idReserva(), 5, 5, "Antes de pagar"));
        e.s.liquidacion().registrarSaldoEfectivo(p.conductorSesion(), p.idReserva());

        assertThrows(DatoInvalidoException.class, () -> e.s.evaluacion().calificar(p.cliente(), p.idReserva(), 6, 5, null));
        assertThrows(AccesoDenegadoException.class, () -> e.s.evaluacion().calificar(e.cliente(), p.idReserva(), 5, 5, null));
        e.s.evaluacion().calificar(p.cliente(), p.idReserva(), 5, 4, "Muy puntuales");
        assertThrows(ReglaNegocioException.class, () -> e.s.evaluacion().calificar(p.cliente(), p.idReserva(), 5, 5, null));

        Empleado calificado = e.repos.empleados().buscarPorId(p.conductor().getIdEmpleado()).orElseThrow();
        assertEquals(4.0, calificado.getCalificacionPromedio(), 0.001);
        var metricas = e.s.evaluacion().consultarMetricas(e.admin);
        assertEquals(1, metricas.totalEvaluaciones());
        assertEquals(5.0, metricas.promedioServicio(), 0.001);
        assertEquals(calificado.getNombre(), metricas.empleados().get(0).nombre());
    }

    @Test
    void us06_losConductoresAsignadosSeConservanAlListarLaFlota() {
        Vehiculo v = e.repos.vehiculos().buscarPorPlaca("ABC123").orElseThrow();
        e.s.flota().asignarConductor(e.admin, v.getPlaca(), conductor.getIdEmpleado());

        assertEquals(List.of(conductor.getIdEmpleado()), e.s.flota().listarVehiculos(e.admin).get(0).getIdsConductores());
        assertFalse(e.s.programacion().consultarRecursos(e.admin, reserva.getIdReserva(), fecha).vehiculos().isEmpty());
    }
}
