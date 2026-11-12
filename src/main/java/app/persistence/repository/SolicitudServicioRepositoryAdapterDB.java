package app.persistence.repository;

import app.domain.DesgloseCotizacion;
import app.domain.SolicitudServicio;
import app.persistence.dbconnection.JdbcTemplate;
import app.persistence.mappers.SolicitudServicioMapper;
import app.service.outputports.SolicitudServicioRepositoryPort;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SolicitudServicioRepositoryAdapterDB implements SolicitudServicioRepositoryPort {

    private final JdbcTemplate jdbc;

    public SolicitudServicioRepositoryAdapterDB(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final SolicitudServicioMapper MAPPER = new SolicitudServicioMapper();

    @Override
    public SolicitudServicio guardar(SolicitudServicio s) {
        DesgloseCotizacion c = s.getCotizacion();
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("id_solicitud", s.getIdSolicitud());
        f.put("id_cliente", s.getIdCliente());
        f.put("tipo_servicio", s.getTipoServicio());
        f.put("estado", s.getEstado());
        f.put("fecha", s.getFecha());
        f.put("origen_municipio", s.getOrigen().municipio());
        f.put("origen_direccion", s.getOrigen().direccion());
        f.put("origen_piso", s.getOrigen().piso());
        f.put("origen_ascensor", s.getOrigen().tieneAscensor());
        f.put("destino_municipio", s.getDestino().municipio());
        f.put("destino_direccion", s.getDestino().direccion());
        f.put("destino_piso", s.getDestino().piso());
        f.put("destino_ascensor", s.getDestino().tieneAscensor());
        f.put("volumen_m3", s.getVolumenM3());
        f.put("enseres", SolicitudServicioMapper.escribirEnseres(s.getEnseres()));
        f.put("tipo_carga", s.getTipoCarga());
        f.put("ayudantes", s.getAyudantes());
        f.put("tarifa_base", c.tarifaBase());
        f.put("recargo_distancia", c.recargoDistancia());
        f.put("recargo_pisos", c.recargoPisos());
        f.put("recargo_personal", c.recargoPersonal());
        f.put("total", c.total());
        f.put("vehiculo_sugerido", c.vehiculoSugerido());
        f.put("distancia_km", c.distanciaKm());
        f.put("duracion_minutos", c.duracionMinutos());
        f.put("fecha_creacion", s.getFechaCreacion());
        jdbc.upsert("solicitud_servicio", f, "id_solicitud");
        return s;
    }

    @Override
    public Optional<SolicitudServicio> buscarPorId(String idSolicitud) {
        return jdbc.queryOne("SELECT * FROM solicitud_servicio WHERE id_solicitud = ?", MAPPER, idSolicitud);
    }

    @Override
    public List<SolicitudServicio> listarPorCliente(String idCliente) {
        return jdbc.query("SELECT * FROM solicitud_servicio WHERE id_cliente = ? ORDER BY fecha_creacion", MAPPER, idCliente);
    }
}
