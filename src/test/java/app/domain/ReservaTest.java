package app.domain;

import app.domain.enums.EstadoReserva;
import app.domain.exceptions.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaTest {

    private Reserva reserva(EstadoReserva estado) {
        Reserva reserva = new Reserva();
        reserva.setIdReserva("RES-001");
        reserva.setEstado(estado);
        reserva.setFechaServicio(LocalDateTime.of(2026, 11, 10, 9, 0));
        reserva.setDuracionMinutos(120);
        return reserva;
    }

    @Test
    void losHitosAvanzanEnOrdenHastaDestino() {
        Reserva reserva = reserva(EstadoReserva.CONFIRMADO);

        assertEquals(EstadoReserva.EN_DESPLAZAMIENTO, reserva.avanzarHito());
        assertEquals(EstadoReserva.EN_ORIGEN, reserva.avanzarHito());
        assertEquals(EstadoReserva.EN_DESTINO, reserva.avanzarHito());
        assertThrows(ReglaNegocioException.class, reserva::avanzarHito);
    }

    @Test
    void soloSeProgramaUnaReservaEnEstadoReservado() {
        Reserva reserva = reserva(EstadoReserva.PENDIENTE_ANTICIPO);

        assertThrows(ReglaNegocioException.class, () -> reserva.programar(LocalDateTime.of(2026, 11, 10, 9, 0),
                "ABC123", "EMP-1", List.of()));
    }

    @Test
    void soloFinalizaDesdeEnDestino() {
        assertThrows(ReglaNegocioException.class, reserva(EstadoReserva.EN_ORIGEN)::finalizar);
    }

    @Test
    void cadaEstadoSeTraduceALaEtapaDelCliente() {
        assertEquals("Pendiente", EstadoReserva.CONFIRMADO.getEtapaCliente());
        assertEquals("En Trayecto", EstadoReserva.EN_ORIGEN.getEtapaCliente());
        assertEquals("Finalizada", EstadoReserva.FINALIZADO.getEtapaCliente());
    }

    @Test
    void detectaTraslapesPeroNoServiciosContiguos() {
        Reserva reserva = reserva(EstadoReserva.CONFIRMADO);

        assertTrue(reserva.seTraslapaCon(LocalDateTime.of(2026, 11, 10, 10, 0), LocalDateTime.of(2026, 11, 10, 12, 0)));
        assertFalse(reserva.seTraslapaCon(LocalDateTime.of(2026, 11, 10, 11, 0), LocalDateTime.of(2026, 11, 10, 13, 0)));
    }
}
