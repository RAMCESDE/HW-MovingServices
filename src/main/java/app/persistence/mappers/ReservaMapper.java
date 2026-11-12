package app.persistence.mappers;

import app.domain.Reserva;
import app.domain.enums.EstadoReserva;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ReservaMapper implements RowMapper<Reserva> {

    @Override
    public Reserva mapRow(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(rs.getString("id_reserva"));
        reserva.setIdSolicitud(rs.getString("id_solicitud"));
        reserva.setIdCliente(rs.getString("id_cliente"));
        reserva.setFechaReserva(rs.getObject("fecha_reserva", LocalDateTime.class));
        reserva.setFechaServicio(rs.getObject("fecha_servicio", LocalDateTime.class));
        reserva.setDuracionMinutos(rs.getInt("duracion_minutos"));
        reserva.setTotal(rs.getBigDecimal("total"));
        reserva.setEstado(EstadoReserva.valueOf(rs.getString("estado")));
        reserva.setPlacaVehiculo(rs.getString("placa_vehiculo"));
        reserva.setIdConductor(rs.getString("id_conductor"));
        return reserva;
    }
}
