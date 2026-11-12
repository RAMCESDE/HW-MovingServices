package app.persistence.repository;

import app.domain.Empleado;
import app.persistence.dbconnection.JdbcTemplate;
import app.persistence.mappers.EmpleadoMapper;
import app.service.outputports.EmpleadoRepositoryPort;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmpleadoRepositoryAdapterDB implements EmpleadoRepositoryPort {

    private final JdbcTemplate jdbc;

    public EmpleadoRepositoryAdapterDB(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final EmpleadoMapper MAPPER = new EmpleadoMapper();

    @Override
    public Empleado guardar(Empleado e) {
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("id_empleado", e.getIdEmpleado());
        f.put("nombre", e.getNombre());
        f.put("cedula", e.getCedula());
        f.put("telefono", e.getTelefono());
        f.put("email", e.getEmail());
        f.put("cargo", e.getCargo());
        f.put("turno", e.getTurno());
        f.put("edad", e.getEdad());
        f.put("sexo", e.getSexo());
        f.put("estado_civil", e.getEstadoCivil());
        f.put("eps", e.getEps());
        f.put("tipo_licencia", e.getTipoLicencia());
        f.put("vencimiento_licencia", e.getVencimientoLicencia());
        f.put("talla_dotacion", e.getTallaDotacion());
        f.put("estado", e.getEstado());
        f.put("suma_calificaciones", e.getSumaCalificaciones());
        f.put("total_calificaciones", e.getTotalCalificaciones());
        jdbc.upsert("empleado", f, "id_empleado");
        return e;
    }

    @Override
    public Optional<Empleado> buscarPorId(String idEmpleado) {
        return jdbc.queryOne("SELECT * FROM empleado WHERE id_empleado = ?", MAPPER, idEmpleado);
    }

    @Override
    public Optional<Empleado> buscarPorCedula(String cedula) {
        return jdbc.queryOne("SELECT * FROM empleado WHERE cedula = ?", MAPPER, cedula);
    }

    @Override
    public List<Empleado> listar() {
        return jdbc.query("SELECT * FROM empleado ORDER BY nombre", MAPPER);
    }
}
