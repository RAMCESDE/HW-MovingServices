package app.domain;

import app.domain.enums.EstadoSolicitud;
import app.domain.exceptions.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SolicitudServicioTest {

    private SolicitudServicio solicitud(EstadoSolicitud estado) {
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setIdSolicitud("SOL-001");
        solicitud.setEstado(estado);
        return solicitud;
    }

    @Test
    void unBorradorSePuedeRetomarYAceptar() {
        SolicitudServicio solicitud = solicitud(EstadoSolicitud.COTIZADA);
        solicitud.guardarBorrador();
        solicitud.aceptar();

        assertEquals(EstadoSolicitud.ACEPTADA, solicitud.getEstado());
    }

    @Test
    void unaCotizacionRechazadaNoSePuedeAceptar() {
        SolicitudServicio solicitud = solicitud(EstadoSolicitud.COTIZADA);
        solicitud.rechazar();

        assertThrows(ReglaNegocioException.class, solicitud::aceptar);
    }

    @Test
    void soloSeReservaUnaCotizacionAceptada() {
        assertThrows(ReglaNegocioException.class, solicitud(EstadoSolicitud.COTIZADA)::marcarReservada);
    }
}
