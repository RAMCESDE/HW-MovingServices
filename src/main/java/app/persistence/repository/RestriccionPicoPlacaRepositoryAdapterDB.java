package app.persistence.repository;

import app.domain.RestriccionPicoPlaca;
import app.persistence.dbconnection.JdbcTemplate;
import app.persistence.mappers.RestriccionPicoPlacaMapper;
import app.service.outputports.RestriccionPicoPlacaRepositoryPort;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestriccionPicoPlacaRepositoryAdapterDB implements RestriccionPicoPlacaRepositoryPort {

    private final JdbcTemplate jdbc;

    public RestriccionPicoPlacaRepositoryAdapterDB(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RestriccionPicoPlacaMapper MAPPER = new RestriccionPicoPlacaMapper();

    @Override
    public RestriccionPicoPlaca guardar(RestriccionPicoPlaca r) {
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("id", r.id());
        f.put("dia", r.dia());
        f.put("digitos", r.digitos().stream().sorted().map(String::valueOf).collect(Collectors.joining(",")));
        f.put("hora_inicio", r.horaInicio());
        f.put("hora_fin", r.horaFin());
        f.put("vigente_desde", r.vigenteDesde());
        f.put("vigente_hasta", r.vigenteHasta());
        jdbc.upsert("restriccion_pico_placa", f, "id");
        return r;
    }

    @Override
    public List<RestriccionPicoPlaca> listar() {
        return jdbc.query("SELECT * FROM restriccion_pico_placa", MAPPER);
    }

    @Override
    public boolean eliminar(String id) {
        return jdbc.update("DELETE FROM restriccion_pico_placa WHERE id = ?", id) > 0;
    }
}
