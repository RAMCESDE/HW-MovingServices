package app.persistence.mappers;

import app.domain.Vehiculo;
import app.domain.enums.EstadoVehiculo;
import app.domain.enums.TipoVehiculo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class VehiculoMapper implements RowMapper<Vehiculo> {

    @Override
    public Vehiculo mapRow(ResultSet rs) throws SQLException {
        Vehiculo vehiculo = new Vehiculo(rs.getString("placa"), TipoVehiculo.valueOf(rs.getString("tipo")),
                rs.getString("modelo"), rs.getDouble("capacidad_carga_kg"), rs.getDouble("capacidad_volumen_m3"),
                rs.getObject("vencimiento_soat", LocalDate.class),
                rs.getObject("vencimiento_tecnomecanica", LocalDate.class));
        vehiculo.setEstado(EstadoVehiculo.valueOf(rs.getString("estado")));
        return vehiculo;
    }
}
