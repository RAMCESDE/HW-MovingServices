package app.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ReservaTest {

    @Test
    void constructorConArgumentos_asignaTodosLosCampos() {
        Reserva reserva = new Reserva("RES-001", "2026-08-27", "2026-09-01", "Confirmada");

        assertEquals("RES-001", reserva.getIdReserva());
        assertEquals("2026-08-27", reserva.getFechaReserva());
        assertEquals("2026-09-01", reserva.getFechaServicio());
        assertEquals("Confirmada", reserva.getEstado());
    }

    @Test
    void constructorPorDefecto_dejaLosCamposEnNull() {
        Reserva reserva = new Reserva();

        assertNull(reserva.getIdReserva());
        assertNull(reserva.getFechaReserva());
        assertNull(reserva.getFechaServicio());
        assertNull(reserva.getEstado());
    }

    @Test
    void setters_actualizanLosValoresDeLosCampos() {
        Reserva reserva = new Reserva();

        reserva.setIdReserva("RES-002");
        reserva.setFechaReserva("2026-08-28");
        reserva.setFechaServicio("2026-09-05");
        reserva.setEstado("Pendiente");

        assertEquals("RES-002", reserva.getIdReserva());
        assertEquals("2026-08-28", reserva.getFechaReserva());
        assertEquals("2026-09-05", reserva.getFechaServicio());
        assertEquals("Pendiente", reserva.getEstado());
    }
}