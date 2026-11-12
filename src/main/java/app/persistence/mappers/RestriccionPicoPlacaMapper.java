package app.persistence.mappers;

import app.domain.RestriccionPicoPlaca;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.Collectors;

public class RestriccionPicoPlacaMapper implements RowMapper<RestriccionPicoPlaca> {

    @Override
    public RestriccionPicoPlaca mapRow(ResultSet rs) throws SQLException {
        return new RestriccionPicoPlaca(rs.getString("id"), DayOfWeek.valueOf(rs.getString("dia")),
                Arrays.stream(rs.getString("digitos").split(",")).map(Integer::valueOf).collect(Collectors.toSet()),
                rs.getObject("hora_inicio", LocalTime.class), rs.getObject("hora_fin", LocalTime.class),
                rs.getObject("vigente_desde", LocalDate.class), rs.getObject("vigente_hasta", LocalDate.class));
    }
}
