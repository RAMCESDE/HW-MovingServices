package app.persistence.mappers;

import app.domain.Evaluacion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class EvaluacionMapper implements RowMapper<Evaluacion> {

    @Override
    public Evaluacion mapRow(ResultSet rs) throws SQLException {
        return new Evaluacion(rs.getString("id_evaluacion"), rs.getString("id_reserva"),
                rs.getInt("puntuacion_servicio"), rs.getInt("puntuacion_personal"), rs.getString("comentario"),
                rs.getObject("fecha", LocalDateTime.class));
    }
}
