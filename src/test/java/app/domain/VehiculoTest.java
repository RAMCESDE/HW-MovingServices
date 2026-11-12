package app.domain;

import app.domain.enums.EstadoDocumento;
import app.domain.enums.EstadoVehiculo;
import app.domain.enums.TipoDocumentoVehiculo;
import app.domain.enums.TipoVehiculo;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehiculoTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 2);

    private Vehiculo vehiculo(String placa, LocalDate soat, LocalDate tecno) {
        return new Vehiculo(placa, TipoVehiculo.NPR, "2022", 4000, 28, soat, tecno);
    }

    @Test
    void elDocumentoAlertaDesdeQuinceDiasAntes() {
        Vehiculo v = vehiculo("ABC123", HOY.plusDays(15), HOY.plusDays(16));

        assertEquals(EstadoDocumento.PROXIMO_A_VENCER, v.estadoDocumento(TipoDocumentoVehiculo.SOAT, HOY));
        assertEquals(EstadoDocumento.VIGENTE, v.estadoDocumento(TipoDocumentoVehiculo.TECNOMECANICA, HOY));
    }

    @Test
    void unDocumentoVencidoInhabilitaYRenovarloRehabilita() {
        Vehiculo v = vehiculo("ABC123", HOY.plusDays(90), HOY.minusDays(1));
        v.sincronizarEstadoConDocumentos(HOY);
        assertEquals(EstadoVehiculo.INHABILITADO_DOCUMENTACION, v.getEstado());

        v.setVencimientoTecnomecanica(HOY.plusYears(1));
        v.sincronizarEstadoConDocumentos(HOY);
        assertEquals(EstadoVehiculo.DISPONIBLE, v.getEstado());
    }

    @Test
    void elDocumentoSigueVigenteElDiaDeSuVencimiento() {
        Vehiculo v = vehiculo("ABC123", HOY, HOY);

        assertEquals(EstadoDocumento.PROXIMO_A_VENCER, v.estadoDocumento(TipoDocumentoVehiculo.SOAT, HOY));
        assertTrue(v.documentosVigentesEn(HOY));
    }

    @Test
    void elDigitoDePicoYPlacaFuncionaConPlacasQueTerminanEnLetra() {
        assertEquals(7, vehiculo("ABC127", HOY, HOY).ultimoDigitoPlaca());
        assertEquals(3, vehiculo("123ABC", HOY, HOY).ultimoDigitoPlaca());
    }

    @Test
    void picoYPlacaRestringeSoloElDiaDigitoHorarioYVigenciaIndicados() {
        RestriccionPicoPlaca r = new RestriccionPicoPlaca("P1", DayOfWeek.MONDAY, Set.of(7), LocalTime.of(6, 0),
                LocalTime.of(10, 0), HOY, HOY.plusMonths(6));
        Vehiculo restringido = vehiculo("ABC127", HOY, HOY);
        LocalDateTime lunes9 = LocalDateTime.of(2026, 11, 9, 9, 0);

        assertTrue(r.restringe(restringido, lunes9, lunes9.plusHours(3)));
        assertFalse(r.restringe(vehiculo("ABC128", HOY, HOY), lunes9, lunes9.plusHours(3)), "otro dígito");
        assertFalse(r.restringe(restringido, lunes9.plusDays(1), lunes9.plusDays(1).plusHours(3)), "otro día");
        assertFalse(r.restringe(restringido, lunes9.plusHours(2), lunes9.plusHours(4)), "después de la franja");
        assertFalse(r.restringe(restringido, lunes9.plusYears(1), lunes9.plusYears(1).plusHours(3)), "fuera de vigencia");
    }
}
