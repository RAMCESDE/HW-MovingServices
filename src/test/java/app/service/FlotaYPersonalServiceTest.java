package app.service;

import app.domain.AlertaDocumento;
import app.domain.Empleado;
import app.domain.enums.EstadoDocumento;
import app.domain.enums.EstadoEmpleado;
import app.domain.enums.EstadoVehiculo;
import app.domain.enums.TipoDocumentoVehiculo;
import app.domain.enums.TipoVehiculo;
import app.domain.exceptions.AccesoDenegadoException;
import app.domain.exceptions.DatoInvalidoException;
import app.domain.exceptions.ReglaNegocioException;
import app.service.inputports.dto.NuevaRestriccionPicoPlaca;
import app.service.inputports.dto.RegistroVehiculo;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Épica 2: US-05 a US-10. */
class FlotaYPersonalServiceTest {

    private final Entorno e = new Entorno();
    private final LocalDate hoy = Entorno.AHORA.toLocalDate();

    private RegistroVehiculo datos(String placa, LocalDate soat, LocalDate tecno) {
        return new RegistroVehiculo(placa, TipoVehiculo.NPR, "2022", 4000, 28, soat, tecno);
    }

    @Test
    void us05_registraVehiculoDisponibleYNormalizaLaPlaca() {
        var v = e.s.flota().registrarVehiculo(e.admin, datos("abc-123", hoy.plusYears(1), hoy.plusYears(1)));

        assertEquals("ABC123", v.getPlaca());
        assertEquals(EstadoVehiculo.DISPONIBLE, v.getEstado());
        assertEquals(1, e.s.flota().listarVehiculos(e.admin).size());
    }

    @Test
    void us05_rechazaPlacaDuplicadaInvalidaOCapacidadCero() {
        e.s.flota().registrarVehiculo(e.admin, datos("ABC123", hoy.plusYears(1), hoy.plusYears(1)));

        assertThrows(ReglaNegocioException.class,
                () -> e.s.flota().registrarVehiculo(e.admin, datos("ABC123", hoy.plusYears(1), hoy.plusYears(1))));
        assertThrows(DatoInvalidoException.class,
                () -> e.s.flota().registrarVehiculo(e.admin, datos("X1", hoy.plusYears(1), hoy.plusYears(1))));
        assertThrows(DatoInvalidoException.class, () -> e.s.flota().registrarVehiculo(e.admin,
                new RegistroVehiculo("DEF456", TipoVehiculo.LUV, "2022", 0, 12, hoy, hoy)));
    }

    @Test
    void us06_asignaYRetiraConductoresDeUnVehiculo() {
        e.vehiculo("ABC123", 12);
        Empleado c1 = e.conductor();
        Empleado c2 = e.conductor();

        e.s.flota().asignarConductor(e.admin, "ABC123", c1.getIdEmpleado());
        e.s.flota().asignarConductor(e.admin, "ABC123", c2.getIdEmpleado());
        assertEquals(2, e.repos.vehiculos().buscarPorPlaca("ABC123").orElseThrow().getIdsConductores().size());

        e.s.flota().retirarConductor(e.admin, "ABC123", c1.getIdEmpleado());
        assertEquals(List.of(c2.getIdEmpleado()),
                e.repos.vehiculos().buscarPorPlaca("ABC123").orElseThrow().getIdsConductores());
    }

    @Test
    void us06_noAsignaAuxiliaresNiConductoresInactivos() {
        e.vehiculo("ABC123", 12);
        Empleado auxiliar = e.auxiliar();
        Empleado inactivo = e.conductor();
        e.s.empleado().cambiarEstado(e.admin, inactivo.getIdEmpleado(), EstadoEmpleado.INACTIVO);

        assertThrows(ReglaNegocioException.class, () -> e.s.flota().asignarConductor(e.admin, "ABC123", auxiliar.getIdEmpleado()));
        assertThrows(ReglaNegocioException.class, () -> e.s.flota().asignarConductor(e.admin, "ABC123", inactivo.getIdEmpleado()));
    }

    @Test
    void us07_alertaDocumentosProximosEInhabilitaLosVencidos() {
        e.s.flota().registrarVehiculo(e.admin, datos("AAA111", hoy.plusDays(10), hoy.plusYears(1)));
        e.s.flota().registrarVehiculo(e.admin, datos("BBB222", hoy.plusYears(1), hoy.plusYears(1)));
        e.s.flota().registrarVehiculo(e.admin, datos("CCC333", hoy.plusYears(1), hoy.plusDays(20)));
        e.reloj.avanzar(java.time.Duration.ofDays(21));

        List<AlertaDocumento> alertas = e.s.documentos().revisarDocumentos(e.admin);

        AlertaDocumento vencido = alertas.get(0);
        assertEquals("AAA111", vencido.placa());
        assertEquals(EstadoDocumento.VENCIDO, vencido.estado());
        assertEquals(TipoDocumentoVehiculo.SOAT, vencido.documento());
        assertEquals(EstadoVehiculo.INHABILITADO_DOCUMENTACION,
                e.repos.vehiculos().buscarPorPlaca("AAA111").orElseThrow().getEstado());
        assertEquals(EstadoVehiculo.INHABILITADO_DOCUMENTACION,
                e.repos.vehiculos().buscarPorPlaca("CCC333").orElseThrow().getEstado());
        assertTrue(alertas.stream().noneMatch(a -> a.placa().equals("BBB222")));
    }

    @Test
    void us07_renovarDocumentosRehabilitaElVehiculo() {
        e.s.flota().registrarVehiculo(e.admin, datos("AAA111", hoy.minusDays(1), hoy.plusYears(1)));
        assertEquals(EstadoVehiculo.INHABILITADO_DOCUMENTACION,
                e.repos.vehiculos().buscarPorPlaca("AAA111").orElseThrow().getEstado());

        var v = e.s.documentos().actualizarDocumentos(e.admin, "AAA111", hoy.plusYears(1), hoy.plusYears(1));

        assertEquals(EstadoVehiculo.DISPONIBLE, v.getEstado());
    }

    @Test
    void us08_parametrizaYConsultaPicoYPlacaPorVehiculo() {
        e.vehiculo("ABC127", 12);
        e.vehiculo("ABC128", 12);
        var r = e.s.picoPlaca().crearRestriccion(e.admin, new NuevaRestriccionPicoPlaca(DayOfWeek.MONDAY, Set.of(1, 7),
                LocalTime.of(5, 0), LocalTime.of(20, 0), hoy, hoy.plusMonths(6)));
        LocalDateTime lunes = LocalDateTime.of(2026, 11, 9, 9, 0);

        assertEquals(Set.of(1, 7), e.s.picoPlaca().listarRestricciones(e.admin).get(0).digitos());
        assertTrue(e.s.picoPlaca().vehiculoRestringido(e.admin, "ABC127", lunes, lunes.plusHours(2)));
        assertFalse(e.s.picoPlaca().vehiculoRestringido(e.admin, "ABC128", lunes, lunes.plusHours(2)));

        e.s.picoPlaca().eliminarRestriccion(e.admin, r.id());
        assertFalse(e.s.picoPlaca().vehiculoRestringido(e.admin, "ABC127", lunes, lunes.plusHours(2)));
    }

    @Test
    void us08_rechazaParametrosInvalidos() {
        assertThrows(DatoInvalidoException.class, () -> e.s.picoPlaca().crearRestriccion(e.admin,
                new NuevaRestriccionPicoPlaca(DayOfWeek.MONDAY, Set.of(11), LocalTime.of(5, 0), LocalTime.of(20, 0), hoy, hoy)));
        assertThrows(DatoInvalidoException.class, () -> e.s.picoPlaca().crearRestriccion(e.admin,
                new NuevaRestriccionPicoPlaca(DayOfWeek.MONDAY, Set.of(1), LocalTime.of(20, 0), LocalTime.of(5, 0), hoy, hoy)));
    }

    @Test
    void us09_registraEmpleadoConDatosYPermiteSuAcceso() {
        Empleado conductor = e.conductor();

        Empleado guardado = e.repos.empleados().buscarPorId(conductor.getIdEmpleado()).orElseThrow();
        assertEquals("Sura", guardado.getEps());
        assertEquals("C2", guardado.getTipoLicencia());
        assertEquals("L", guardado.getTallaDotacion());
        assertEquals(EstadoEmpleado.ACTIVO, guardado.getEstado());
        assertEquals(conductor.getIdEmpleado(), e.sesionDe(conductor).idPerfil());
    }

    @Test
    void us09_exigeLicenciaAlConductorYDocumentoUnico() {
        assertThrows(DatoInvalidoException.class, () -> e.empleado(app.domain.enums.CargoEmpleado.CONDUCTOR, null, null));
        Empleado auxiliar = e.auxiliar();
        var duplicado = new app.service.inputports.dto.RegistroEmpleado("Otro", auxiliar.getCedula(), "3001112222",
                "otro@hw.com", Entorno.CLAVE, app.domain.enums.CargoEmpleado.AUXILIAR_CARGA, null, 25, null, null, null, null, null, null);
        assertThrows(ReglaNegocioException.class, () -> e.s.empleado().registrarEmpleado(e.admin, duplicado));
    }

    @Test
    void us10_inactivarBloqueaElAccesoYReactivarLoRestablece() {
        Empleado auxiliar = e.auxiliar();

        e.s.empleado().cambiarEstado(e.admin, auxiliar.getIdEmpleado(), EstadoEmpleado.INACTIVO);
        assertThrows(AccesoDenegadoException.class, () -> e.sesionDe(auxiliar));

        e.s.empleado().cambiarEstado(e.admin, auxiliar.getIdEmpleado(), EstadoEmpleado.ACTIVO);
        assertEquals(auxiliar.getIdEmpleado(), e.sesionDe(auxiliar).idPerfil());
    }

    @Test
    void us10_noDisponibleConservaElAcceso() {
        Empleado auxiliar = e.auxiliar();

        e.s.empleado().cambiarEstado(e.admin, auxiliar.getIdEmpleado(), EstadoEmpleado.NO_DISPONIBLE);

        assertEquals(auxiliar.getIdEmpleado(), e.sesionDe(auxiliar).idPerfil());
    }
}
