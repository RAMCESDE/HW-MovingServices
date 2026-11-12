package app.persistence.mappers;

import app.domain.Cliente;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteMapper implements RowMapper<Cliente> {

    @Override
    public Cliente mapRow(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(rs.getString("nombre"), rs.getString("cedula"), rs.getString("telefono"),
                rs.getString("email"), rs.getString("direccion"));
        cliente.setIdCliente(rs.getString("id_cliente"));
        return cliente;
    }
}
