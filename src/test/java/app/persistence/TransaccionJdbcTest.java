package app.persistence;

import app.domain.Cliente;
import app.persistence.dbconnection.JdbcTemplate;
import app.persistence.dbconnection.TransaccionJdbcAdapter;
import app.persistence.repository.ClienteRepositoryAdapterDB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Solo corre con HW_TEST_DB_URL: comprueba el rollback contra una base de datos real. */
@EnabledIfEnvironmentVariable(named = "HW_TEST_DB_URL", matches = ".+")
class TransaccionJdbcTest {

    @Test
    void unErrorDentroDeLaTransaccionNoDejaDatosGuardados() throws SQLException {
        try (Connection connection = DriverManager.getConnection(System.getenv("HW_TEST_DB_URL"),
                System.getenv("HW_TEST_DB_USER"), System.getenv("HW_TEST_DB_PASSWORD"))) {
            var repositorio = new ClienteRepositoryAdapterDB(new JdbcTemplate(connection));
            Cliente cliente = new Cliente();
            cliente.setIdCliente("CLI-ROLLBACK");
            cliente.setEmail("rollback@correo.com");

            assertThrows(IllegalStateException.class, () -> new TransaccionJdbcAdapter(connection).ejecutar(() -> {
                repositorio.guardar(cliente);
                throw new IllegalStateException("falla a mitad de la operación");
            }));

            assertTrue(repositorio.buscarPorId("CLI-ROLLBACK").isEmpty());
        }
    }
}
