package app.persistence.dbconnection;

import app.domain.exceptions.PersistenciaException;
import app.service.outputports.TransaccionPort;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

public class TransaccionJdbcAdapter implements TransaccionPort {

    private final Connection connection;

    public TransaccionJdbcAdapter(Connection connection) {
        this.connection = connection;
    }

    @Override
    public <T> T ejecutar(Supplier<T> operacion) {
        try {
            connection.setAutoCommit(false);
            try {
                T resultado = operacion.get();
                connection.commit();
                return resultado;
            } catch (RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error en la transacción: " + e.getMessage());
        }
    }
}
