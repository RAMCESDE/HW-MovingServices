package app.service;

import app.domain.DesgloseCotizacion;
import app.domain.Sesion;
import app.domain.SolicitudServicio;
import app.domain.Ubicacion;
import app.domain.enums.Enser;
import app.domain.enums.EstadoPago;
import app.domain.enums.EstadoReserva;
import app.domain.enums.EstadoSolicitud;
import app.domain.enums.MetodoPago;
import app.domain.enums.Municipio;
import app.domain.enums.TipoCarga;
import app.domain.enums.TipoServicio;
import app.domain.enums.TipoVehiculo;
import app.domain.exceptions.AccesoDenegadoException;
import app.domain.exceptions.DatoInvalidoException;
import app.domain.exceptions.PagoRechazadoException;
import app.domain.exceptions.ReglaNegocioException;
import app.service.inputports.dto.SolicitudCotizacion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Épica 3: US-11, US-12 y US-13. */
class CotizacionYReservaServiceTest {

    private final Entorno e = new Entorno();

    @Test
    void us11_desgloseConTarifaDistanciaPisosYPersonal() {
        Sesion cliente = e.cliente();
        SolicitudCotizacion datos = new SolicitudCotizacion(TipoServicio.MUDANZA, Entorno.FECHA_SERVICIO.toLocalDate(),
                new Ubicacion(Municipio.MEDELLIN, "Calle 1", 3, false), new Ubicacion(Municipio.MEDELLIN, "Calle 2", 2, false),
                5, Map.of(), TipoCarga.PESADA, 2);

        DesgloseCotizacion c = e.s.cotizacion().cotizar(cliente, datos).getCotizacion();

        // LUV por 5 m³; mismo municipio = 6 km; 2 + 1 pisos por escalera; carga pesada factor 1,6.
        assertEquals(TipoVehiculo.LUV, c.vehiculoSugerido());
        assertEquals(0, new BigDecimal("120000").compareTo(c.tarifaBase()));
        assertEquals(0, new BigDecimal("15000").compareTo(c.recargoDistancia()));
        assertEquals(0, new BigDecimal("57600").compareTo(c.recargoPisos()));
        assertEquals(0, new BigDecimal("140000").compareTo(c.recargoPersonal()));
        assertEquals(0, new BigDecimal("332600").compareTo(c.total()));
    }

    @Test
    void us11_elAscensorEliminaElRecargoPorPisos() {
        SolicitudCotizacion datos = new SolicitudCotizacion(TipoServicio.MUDANZA, Entorno.FECHA_SERVICIO.toLocalDate(),
                new Ubicacion(Municipio.MEDELLIN, "Calle 1", 8, true), new Ubicacion(Municipio.MEDELLIN, "Calle 2", 1, false),
                5, Map.of(), TipoCarga.PESADA, 0);

        assertEquals(0, e.s.cotizacion().cotizar(e.cliente(), datos).getCotizacion().recargoPisos().signum());
    }

    @Test
    void us11_calculaElVolumenConLaListaDeEnseresYSugiereVehiculo() {
        SolicitudCotizacion datos = new SolicitudCotizacion(TipoServicio.ACARREO, Entorno.FECHA_SERVICIO.toLocalDate(),
                new Ubicacion(Municipio.BELLO, "Calle 1", 1, false), new Ubicacion(Municipio.ITAGUI, "Calle 2", 1, false),
                0, Map.of(Enser.CAJA, 10, Enser.TELEVISOR, 1), TipoCarga.LIVIANA, 0);

        SolicitudServicio solicitud = e.s.cotizacion().cotizar(e.cliente(), datos);

        assertEquals(1.2, solicitud.getVolumenM3(), 0.0001);
        assertEquals(TipoVehiculo.MOTOCARRO, solicitud.getCotizacion().vehiculoSugerido());
        assertEquals(0, new BigDecimal("51000").compareTo(solicitud.getCotizacion().tarifaBase()), "factor de acarreo");
        assertTrue(solicitud.getCotizacion().distanciaKm() > 15);
        assertEquals(Map.of(Enser.CAJA, 10, Enser.TELEVISOR, 1),
                e.repos.solicitudes().buscarPorId(solicitud.getIdSolicitud()).orElseThrow().getEnseres());
    }

    @Test
    void us11_validaFechaVolumenYCapacidadMaxima() {
        Sesion cliente = e.cliente();
        var base = e.datosCotizacion(0, 5);

        assertThrows(DatoInvalidoException.class, () -> e.s.cotizacion().cotizar(cliente, new SolicitudCotizacion(base.tipoServicio(),
                Entorno.AHORA.toLocalDate().minusDays(1), base.origen(), base.destino(), 5, Map.of(), base.tipoCarga(), 0)));
        assertThrows(DatoInvalidoException.class, () -> e.s.cotizacion().cotizar(cliente, e.datosCotizacion(0, 0)));
        assertThrows(ReglaNegocioException.class, () -> e.s.cotizacion().cotizar(cliente, e.datosCotizacion(0, 40)));
    }

    @Test
    void us11_guardaBorradorQueSePuedeRetomar() {
        Sesion cliente = e.cliente();
        SolicitudServicio s = e.s.cotizacion().cotizar(cliente, e.datosCotizacion(0, 5));

        e.s.cotizacion().guardarBorrador(cliente, s.getIdSolicitud());
        assertEquals(EstadoSolicitud.BORRADOR, e.s.cotizacion().listarMisCotizaciones(cliente).get(0).getEstado());

        assertEquals(EstadoSolicitud.ACEPTADA, e.s.cotizacion().aceptar(cliente, s.getIdSolicitud()).getEstado());
    }

    @Test
    void us12_aceptaORechazaSoloSuPropiaCotizacion() {
        Sesion cliente = e.cliente();
        SolicitudServicio s = e.s.cotizacion().cotizar(cliente, e.datosCotizacion(0, 5));

        assertThrows(AccesoDenegadoException.class, () -> e.s.cotizacion().aceptar(e.cliente(), s.getIdSolicitud()));
        assertEquals(EstadoSolicitud.RECHAZADA, e.s.cotizacion().rechazar(cliente, s.getIdSolicitud()).getEstado());
        assertThrows(ReglaNegocioException.class, () -> e.s.cotizacion().aceptar(cliente, s.getIdSolicitud()));
    }

    @Test
    void us13_noReservaSinAceptarLaCotizacion() {
        Sesion cliente = e.cliente();
        SolicitudServicio s = e.s.cotizacion().cotizar(cliente, e.datosCotizacion(0, 5));

        assertThrows(ReglaNegocioException.class, () -> e.s.reserva().reservar(cliente, s.getIdSolicitud(),
                Entorno.FECHA_SERVICIO, MetodoPago.TARJETA, null, "tok_aprobado"));
    }

    @Test
    void us13_tarjetaAprobadaReservaConDiezPorCientoYComprobante() {
        Sesion cliente = e.cliente();
        SolicitudServicio s = e.cotizacionAceptada(cliente, 0, 5);
        BigDecimal total = s.getCotizacion().total();

        var r = e.s.reserva().reservar(cliente, s.getIdSolicitud(), Entorno.FECHA_SERVICIO, MetodoPago.TARJETA, null, "tok_aprobado");

        assertEquals(EstadoReserva.RESERVADO, r.reserva().getEstado());
        assertEquals(EstadoPago.APROBADO, r.pago().getEstadoPago());
        assertTrue(r.pago().getMonto().multiply(BigDecimal.TEN).compareTo(total) >= 0, "mínimo 10%");
        assertTrue(r.comprobante().contains("COMPROBANTE PROVISIONAL"));
        assertEquals(EstadoSolicitud.RESERVADA, e.repos.solicitudes().buscarPorId(s.getIdSolicitud()).orElseThrow().getEstado());
        assertEquals(1, e.s.programacion().listarPorProgramar(e.admin).size(), "queda en el panel del administrador");
    }

    @Test
    void us13_pagoRechazadoNoReservaYPermiteReintentar() {
        Sesion cliente = e.cliente();
        SolicitudServicio s = e.cotizacionAceptada(cliente, 0, 5);

        assertThrows(PagoRechazadoException.class, () -> e.s.reserva().reservar(cliente, s.getIdSolicitud(),
                Entorno.FECHA_SERVICIO, MetodoPago.TARJETA, null, "tok_rechazado"));
        assertTrue(e.s.reserva().listarMisReservas(cliente).isEmpty());

        var r = e.s.reserva().reservar(cliente, s.getIdSolicitud(), Entorno.FECHA_SERVICIO, MetodoPago.TRANSFERENCIA, null, "tok_aprobado");
        assertEquals(EstadoReserva.RESERVADO, r.reserva().getEstado());
    }

    @Test
    void us13_efectivoQuedaPendienteHastaQueElAdministradorLoRegistra() {
        Sesion cliente = e.cliente();
        SolicitudServicio s = e.cotizacionAceptada(cliente, 0, 5);

        var r = e.s.reserva().reservar(cliente, s.getIdSolicitud(), Entorno.FECHA_SERVICIO, MetodoPago.EFECTIVO, null, null);
        assertEquals(EstadoReserva.PENDIENTE_ANTICIPO, r.reserva().getEstado());
        assertEquals(EstadoPago.PENDIENTE, r.pago().getEstadoPago());
        assertTrue(e.s.programacion().listarPorProgramar(e.admin).isEmpty());
        assertEquals(1, e.s.reserva().listarAnticiposPendientes(e.admin).size());

        var confirmada = e.s.reserva().registrarAnticipoEfectivo(e.admin, r.reserva().getIdReserva());

        assertEquals(EstadoReserva.RESERVADO, confirmada.reserva().getEstado());
        assertEquals(EstadoPago.APROBADO, e.repos.pagos().listarPorReserva(r.reserva().getIdReserva()).get(0).getEstadoPago());
        assertEquals(1, e.s.programacion().listarPorProgramar(e.admin).size());
    }

    @Test
    void us13_validaMontoFechaYQueNoSeReserveDosVeces() {
        Sesion cliente = e.cliente();
        SolicitudServicio s = e.cotizacionAceptada(cliente, 0, 5);
        String id = s.getIdSolicitud();
        BigDecimal total = s.getCotizacion().total();

        assertThrows(DatoInvalidoException.class, () -> e.s.reserva().reservar(cliente, id, Entorno.FECHA_SERVICIO, MetodoPago.TARJETA, BigDecimal.ONE, "t"));
        assertThrows(DatoInvalidoException.class, () -> e.s.reserva().reservar(cliente, id, Entorno.FECHA_SERVICIO, MetodoPago.TARJETA, total.add(BigDecimal.ONE), "t"));
        assertThrows(DatoInvalidoException.class, () -> e.s.reserva().reservar(cliente, id, Entorno.AHORA, MetodoPago.TARJETA, null, "t"));

        e.s.reserva().reservar(cliente, id, Entorno.FECHA_SERVICIO, MetodoPago.TARJETA, null, "t");
        assertThrows(ReglaNegocioException.class, () -> e.s.reserva().reservar(cliente, id, Entorno.FECHA_SERVICIO, MetodoPago.TARJETA, null, "t"));
    }
}
