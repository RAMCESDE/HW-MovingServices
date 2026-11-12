package app.persistence.repository;

import app.domain.Vehiculo;
import app.persistence.dbconnection.JdbcTemplate;
import app.persistence.mappers.VehiculoMapper;
import app.service.outputports.VehiculoRepositoryPort;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VehiculoRepositoryAdapterDB implements VehiculoRepositoryPort {

    private final JdbcTemplate jdbc;

    public VehiculoRepositoryAdapterDB(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final VehiculoMapper MAPPER = new VehiculoMapper();

    @Override
    public Vehiculo guardar(Vehiculo v) {
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("placa", v.getPlaca());
        f.put("tipo", v.getTipo());
        f.put("modelo", v.getModelo());
        f.put("capacidad_carga_kg", v.getCapacidadCargaKg());
        f.put("capacidad_volumen_m3", v.getCapacidadVolumenM3());
        f.put("vencimiento_soat", v.getVencimientoSoat());
        f.put("vencimiento_tecnomecanica", v.getVencimientoTecnomecanica());
        f.put("estado", v.getEstado());
        jdbc.upsert("vehiculo", f, "placa");
        jdbc.update("DELETE FROM vehiculo_conductor WHERE placa = ?", v.getPlaca());
        for (String idEmpleado : v.getIdsConductores()) {
            jdbc.update("INSERT INTO vehiculo_conductor (placa, id_empleado) VALUES (?, ?)", v.getPlaca(), idEmpleado);
        }
        return v;
    }

    @Override
    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        return jdbc.queryOne("SELECT * FROM vehiculo WHERE placa = ?", MAPPER, placa).map(this::conConductores);
    }

    @Override
    public List<Vehiculo> listar() {
        return jdbc.query("SELECT * FROM vehiculo ORDER BY placa", MAPPER).stream().map(this::conConductores).toList();
    }

    private Vehiculo conConductores(Vehiculo v) {
        v.setIdsConductores(jdbc.query("SELECT id_empleado FROM vehiculo_conductor WHERE placa = ?",
                rs -> rs.getString(1), v.getPlaca()));
        return v;
    }
}
