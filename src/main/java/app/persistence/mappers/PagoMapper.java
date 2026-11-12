package app.persistence.mappers;

import app.domain.Pago;
import app.domain.enums.EstadoPago;
import app.domain.enums.MetodoPago;
import app.domain.enums.TipoPago;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class PagoMapper implements RowMapper<Pago> {

    @Override
    public Pago mapRow(ResultSet rs) throws SQLException {
        return new Pago(rs.getString("id_pago"), rs.getString("id_reserva"), rs.getBigDecimal("monto"),
                TipoPago.valueOf(rs.getString("tipo_pago")), MetodoPago.valueOf(rs.getString("metodo_pago")),
                EstadoPago.valueOf(rs.getString("estado_pago")), rs.getString("referencia"),
                rs.getObject("fecha", LocalDateTime.class));
    }
}
