package app.configuration;

import app.persistence.dbconnection.JdbcTemplate;
import app.persistence.dbconnection.TransaccionJdbcAdapter;
import app.persistence.repository.ClienteRepositoryAdapter;
import app.persistence.repository.ClienteRepositoryAdapterDB;
import app.persistence.repository.CuentaRepositoryAdapter;
import app.persistence.repository.CuentaRepositoryAdapterDB;
import app.persistence.repository.EmpleadoRepositoryAdapter;
import app.persistence.repository.EmpleadoRepositoryAdapterDB;
import app.persistence.repository.EvaluacionRepositoryAdapter;
import app.persistence.repository.EvaluacionRepositoryAdapterDB;
import app.persistence.repository.EventoServicioRepositoryAdapter;
import app.persistence.repository.EventoServicioRepositoryAdapterDB;
import app.persistence.repository.PagoRepositoryAdapter;
import app.persistence.repository.PagoRepositoryAdapterDB;
import app.persistence.repository.ReservaRepositoryAdapter;
import app.persistence.repository.ReservaRepositoryAdapterDB;
import app.persistence.repository.RestriccionPicoPlacaRepositoryAdapter;
import app.persistence.repository.RestriccionPicoPlacaRepositoryAdapterDB;
import app.persistence.repository.SolicitudServicioRepositoryAdapter;
import app.persistence.repository.SolicitudServicioRepositoryAdapterDB;
import app.persistence.repository.TokenCuentaRepositoryAdapter;
import app.persistence.repository.TokenCuentaRepositoryAdapterDB;
import app.persistence.repository.TransaccionEnMemoriaAdapter;
import app.persistence.repository.VehiculoRepositoryAdapter;
import app.persistence.repository.VehiculoRepositoryAdapterDB;
import app.service.outputports.ClienteRepositoryPort;
import app.service.outputports.CuentaRepositoryPort;
import app.service.outputports.EmpleadoRepositoryPort;
import app.service.outputports.EvaluacionRepositoryPort;
import app.service.outputports.EventoServicioRepositoryPort;
import app.service.outputports.PagoRepositoryPort;
import app.service.outputports.ReservaRepositoryPort;
import app.service.outputports.RestriccionPicoPlacaRepositoryPort;
import app.service.outputports.SolicitudServicioRepositoryPort;
import app.service.outputports.TokenCuentaRepositoryPort;
import app.service.outputports.TransaccionPort;
import app.service.outputports.VehiculoRepositoryPort;

import java.sql.Connection;

/** Conjunto de adaptadores de salida; cambiar de almacenamiento solo toca esta clase. */
public record Repositorios(CuentaRepositoryPort cuentas, TokenCuentaRepositoryPort tokens,
                           ClienteRepositoryPort clientes, EmpleadoRepositoryPort empleados,
                           VehiculoRepositoryPort vehiculos, RestriccionPicoPlacaRepositoryPort restricciones,
                           SolicitudServicioRepositoryPort solicitudes, ReservaRepositoryPort reservas,
                           PagoRepositoryPort pagos, EventoServicioRepositoryPort eventos,
                           EvaluacionRepositoryPort evaluaciones, TransaccionPort transaccion) {

    public static Repositorios enMemoria() {
        return new Repositorios(new CuentaRepositoryAdapter(), new TokenCuentaRepositoryAdapter(),
                new ClienteRepositoryAdapter(), new EmpleadoRepositoryAdapter(), new VehiculoRepositoryAdapter(),
                new RestriccionPicoPlacaRepositoryAdapter(), new SolicitudServicioRepositoryAdapter(),
                new ReservaRepositoryAdapter(), new PagoRepositoryAdapter(), new EventoServicioRepositoryAdapter(),
                new EvaluacionRepositoryAdapter(), new TransaccionEnMemoriaAdapter());
    }

    public static Repositorios enBaseDeDatos(Connection connection) {
        JdbcTemplate jdbc = new JdbcTemplate(connection);
        return new Repositorios(new CuentaRepositoryAdapterDB(jdbc), new TokenCuentaRepositoryAdapterDB(jdbc),
                new ClienteRepositoryAdapterDB(jdbc), new EmpleadoRepositoryAdapterDB(jdbc),
                new VehiculoRepositoryAdapterDB(jdbc), new RestriccionPicoPlacaRepositoryAdapterDB(jdbc),
                new SolicitudServicioRepositoryAdapterDB(jdbc), new ReservaRepositoryAdapterDB(jdbc),
                new PagoRepositoryAdapterDB(jdbc), new EventoServicioRepositoryAdapterDB(jdbc),
                new EvaluacionRepositoryAdapterDB(jdbc), new TransaccionJdbcAdapter(connection));
    }
}
