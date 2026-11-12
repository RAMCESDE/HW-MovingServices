package app.persistence.dbconnection;

import app.domain.exceptions.PersistenciaException;
import app.persistence.mappers.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Concentra el manejo de JDBC para que cada repositorio solo declare su SQL. */
public class JdbcTemplate {

    private final Connection connection;

    public JdbcTemplate(Connection connection) {
        this.connection = connection;
    }

    public int update(String sql, Object... parametros) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            enlazar(ps, parametros);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al escribir datos: " + e.getMessage());
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> mapper, Object... parametros) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            enlazar(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> filas = new ArrayList<>();
                while (rs.next()) {
                    filas.add(mapper.mapRow(rs));
                }
                return filas;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar datos: " + e.getMessage());
        }
    }

    public <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... parametros) {
        return query(sql, mapper, parametros).stream().findFirst();
    }

    /** Inserta la fila o la actualiza si la llave primaria ya existe. */
    public void upsert(String tabla, Map<String, Object> columnas, String... llaves) {
        List<String> nombresLlave = List.of(llaves);
        String nombres = String.join(", ", columnas.keySet());
        String marcas = columnas.keySet().stream().map(c -> "?").collect(Collectors.joining(", "));
        String asignaciones = columnas.keySet().stream().filter(c -> !nombresLlave.contains(c))
                .map(c -> c + " = VALUES(" + c + ")").collect(Collectors.joining(", "));
        update("INSERT INTO " + tabla + " (" + nombres + ") VALUES (" + marcas + ") ON DUPLICATE KEY UPDATE " + asignaciones,
                columnas.values().toArray());
    }

    private void enlazar(PreparedStatement ps, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            Object valor = parametros[i] instanceof Enum<?> e ? e.name() : parametros[i];
            ps.setObject(i + 1, valor);
        }
    }
}
