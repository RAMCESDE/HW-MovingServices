package app.persistence.repository;

import app.domain.Cliente;
import app.service.outputports.ClienteRepositoryPort;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final Map<String, Cliente> datos = new LinkedHashMap<>();

    @Override
    public Cliente guardar(Cliente cliente) {
        datos.put(cliente.getIdCliente(), cliente);
        return cliente;
    }

    @Override
    public Optional<Cliente> buscarPorId(String idCliente) {
        return Optional.ofNullable(datos.get(idCliente));
    }
}
