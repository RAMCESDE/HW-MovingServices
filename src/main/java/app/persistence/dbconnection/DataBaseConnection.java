package app.persistence.dbconnection;

import app.domain.exceptions.PersistenciaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    private static DataBaseConnection instance;
    private final Connection connection;

    private DataBaseConnection(String url, String username, String password) {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new PersistenciaException("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    public static synchronized DataBaseConnection getInstance(String url, String username, String password) {
        if (instance == null) {
            instance = new DataBaseConnection(url, username, password);
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
