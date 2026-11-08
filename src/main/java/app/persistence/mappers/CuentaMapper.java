package app.persistence.mappers;

import app.domain.Cuenta;
import app.domain.enums.Rol;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class CuentaMapper implements RowMapper<Cuenta> {

    @Override
    public Cuenta mapRow(ResultSet rs) throws SQLException {
        Cuenta cuenta = new Cuenta();
        cuenta.setIdCuenta(rs.getString("id_cuenta"));
        cuenta.setEmail(rs.getString("email"));
        cuenta.setPasswordHash(rs.getString("password_hash"));
        cuenta.setRol(Rol.valueOf(rs.getString("rol")));
        cuenta.setIdPerfil(rs.getString("id_perfil"));
        cuenta.setActiva(rs.getBoolean("activa"));
        cuenta.setCorreoVerificado(rs.getBoolean("correo_verificado"));
        cuenta.setFechaCreacion(rs.getObject("fecha_creacion", LocalDateTime.class));
        return cuenta;
    }
}
