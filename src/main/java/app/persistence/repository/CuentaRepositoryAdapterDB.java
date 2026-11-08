package app.persistence.repository;

import app.domain.Cuenta;
import app.persistence.dbconnection.JdbcTemplate;
import app.persistence.mappers.CuentaMapper;
import app.service.outputports.CuentaRepositoryPort;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CuentaRepositoryAdapterDB implements CuentaRepositoryPort {

    private final JdbcTemplate jdbc;

    public CuentaRepositoryAdapterDB(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final CuentaMapper MAPPER = new CuentaMapper();

    @Override
    public Cuenta guardar(Cuenta c) {
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("id_cuenta", c.getIdCuenta());
        f.put("email", c.getEmail());
        f.put("password_hash", c.getPasswordHash());
        f.put("rol", c.getRol());
        f.put("id_perfil", c.getIdPerfil());
        f.put("activa", c.isActiva());
        f.put("correo_verificado", c.isCorreoVerificado());
        f.put("fecha_creacion", c.getFechaCreacion());
        jdbc.upsert("cuenta", f, "id_cuenta");
        return c;
    }

    @Override
    public Optional<Cuenta> buscarPorId(String idCuenta) {
        return jdbc.queryOne("SELECT * FROM cuenta WHERE id_cuenta = ?", MAPPER, idCuenta);
    }

    @Override
    public Optional<Cuenta> buscarPorEmail(String email) {
        return jdbc.queryOne("SELECT * FROM cuenta WHERE email = ?", MAPPER, email);
    }

    @Override
    public Optional<Cuenta> buscarPorPerfil(String idPerfil) {
        return jdbc.queryOne("SELECT * FROM cuenta WHERE id_perfil = ?", MAPPER, idPerfil);
    }
}
