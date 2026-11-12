package app.persistence.mappers;

import app.domain.Empleado;
import app.domain.enums.CargoEmpleado;
import app.domain.enums.EstadoEmpleado;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class EmpleadoMapper implements RowMapper<Empleado> {

    @Override
    public Empleado mapRow(ResultSet rs) throws SQLException {
        Empleado empleado = new Empleado(rs.getString("nombre"), rs.getString("cedula"), rs.getString("telefono"),
                rs.getString("email"), rs.getString("id_empleado"));
        empleado.setCargo(CargoEmpleado.valueOf(rs.getString("cargo")));
        empleado.setTurno(rs.getString("turno"));
        empleado.setEdad(rs.getObject("edad", Integer.class));
        empleado.setSexo(rs.getString("sexo"));
        empleado.setEstadoCivil(rs.getString("estado_civil"));
        empleado.setEps(rs.getString("eps"));
        empleado.setTipoLicencia(rs.getString("tipo_licencia"));
        empleado.setVencimientoLicencia(rs.getObject("vencimiento_licencia", LocalDate.class));
        empleado.setTallaDotacion(rs.getString("talla_dotacion"));
        empleado.setEstado(EstadoEmpleado.valueOf(rs.getString("estado")));
        empleado.setSumaCalificaciones(rs.getInt("suma_calificaciones"));
        empleado.setTotalCalificaciones(rs.getInt("total_calificaciones"));
        return empleado;
    }
}
