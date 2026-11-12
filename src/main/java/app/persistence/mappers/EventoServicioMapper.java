package app.persistence.mappers;

import app.domain.EventoServicio;
import app.domain.enums.EstadoReserva;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class EventoServicioMapper implements RowMapper<EventoServicio> {

    @Override
    public EventoServicio mapRow(ResultSet rs) throws SQLException {
        return new EventoServicio(rs.getString("id_evento"), rs.getString("id_reserva"),
                EstadoReserva.valueOf(rs.getString("estado")), rs.getObject("fecha_hora", LocalDateTime.class),
                rs.getString("id_cuenta_actor"));
    }
}
