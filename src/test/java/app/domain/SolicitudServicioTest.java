package app.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class SolicitudServicioTest {

    @Test
    void constructorConArgumentos_asignaTodosLosCampos() {
        SolicitudServicio solicitud = new SolicitudServicio("SOL-001", "Mudanza residencial", "Pendiente", "2026-08-27");

        assertEquals("SOL-001", solicitud.getIdSolicitud());
        assertEquals("Mudanza residencial", solicitud.getTipoServicio());
        assertEquals("Pendiente", solicitud.getEstado());
        assertEquals("2026-08-27", solicitud.getFecha());
    }

    @Test
    void constructorPorDefecto_dejaLosCamposEnNull() {
        SolicitudServicio solicitud = new SolicitudServicio();

        assertNull(solicitud.getIdSolicitud());
        assertNull(solicitud.getTipoServicio());
        assertNull(solicitud.getEstado());
        assertNull(solicitud.getFecha());
    }

    @Test
    void setters_actualizanLosValoresDeLosCampos() {
        SolicitudServicio solicitud = new SolicitudServicio();

        solicitud.setIdSolicitud("SOL-002");
        solicitud.setTipoServicio("Mudanza corporativa");
        solicitud.setEstado("En proceso");
        solicitud.setFecha("2026-08-28");

        assertEquals("SOL-002", solicitud.getIdSolicitud());
        assertEquals("Mudanza corporativa", solicitud.getTipoServicio());
        assertEquals("En proceso", solicitud.getEstado());
        assertEquals("2026-08-28", solicitud.getFecha());
    }

    @Test
    void registrarSolicitud_noLanzaExcepciones() {
        SolicitudServicio solicitud = new SolicitudServicio();

        assertDoesNotThrow(solicitud::registrarSolicitud);
    }
}