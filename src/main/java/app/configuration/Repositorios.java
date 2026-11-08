package app.configuration;

import app.persistence.dbconnection.JdbcTemplate;
import app.persistence.dbconnection.TransaccionJdbcAdapter;
import app.persistence.repository.ClienteRepositoryAdapter;
import app.persistence.repository.ClienteRepositoryAdapterDB;
import app.persistence.repository.CuentaRepositoryAdapter;
import app.persistence.repository.CuentaRepositoryAdapterDB;
import app.persistence.repository.TokenCuentaRepositoryAdapter;
import app.persistence.repository.TokenCuentaRepositoryAdapterDB;
import app.persistence.repository.TransaccionEnMemoriaAdapter;
import app.service.outputports.ClienteRepositoryPort;
import app.service.outputports.CuentaRepositoryPort;
import app.service.outputports.TokenCuentaRepositoryPort;
import app.service.outputports.TransaccionPort;

import java.sql.Connection;

/** Conjunto de adaptadores de salida; cambiar de almacenamiento solo toca esta clase. */
public record Repositorios(CuentaRepositoryPort cuentas,
                           TokenCuentaRepositoryPort tokens,
                           ClienteRepositoryPort clientes,
                           TransaccionPort transaccion) {

    public static Repositorios enMemoria() {
        return new Repositorios(new CuentaRepositoryAdapter(),
                new TokenCuentaRepositoryAdapter(),
                new ClienteRepositoryAdapter(),
                new TransaccionEnMemoriaAdapter());
    }

    public static Repositorios enBaseDeDatos(Connection connection) {
        JdbcTemplate jdbc = new JdbcTemplate(connection);
        return new Repositorios(new CuentaRepositoryAdapterDB(jdbc),
                new TokenCuentaRepositoryAdapterDB(jdbc),
                new ClienteRepositoryAdapterDB(jdbc),
                new TransaccionJdbcAdapter(connection));
    }
}
