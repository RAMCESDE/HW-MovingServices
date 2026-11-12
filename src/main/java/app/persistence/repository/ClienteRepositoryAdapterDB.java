package app.persistence.repository;

import app.domain.Cliente;
import app.persistence.dbconnection.JdbcTemplate;
import app.persistence.mappers.ClienteMapper;
import app.service.outputports.ClienteRepositoryPort;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ClienteRepositoryAdapterDB implements ClienteRepositoryPort {

    private final JdbcTemplate jdbc;

    public ClienteRepositoryAdapterDB(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final ClienteMapper MAPPER = new ClienteMapper();

    @Override
    public Cliente guardar(Cliente c) {
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("id_cliente", c.getIdCliente());
        f.put("nombre", c.getNombre());
        f.put("cedula", c.getCedula());
        f.put("telefono", c.getTelefono());
        f.put("email", c.getEmail());
        f.put("direccion", c.getDireccion());
        jdbc.upsert("cliente", f, "id_cliente");
        return c;
    }

    @Override
    public Optional<Cliente> buscarPorId(String idCliente) {
        return jdbc.queryOne("SELECT * FROM cliente WHERE id_cliente = ?", MAPPER, idCliente);
    }
}
