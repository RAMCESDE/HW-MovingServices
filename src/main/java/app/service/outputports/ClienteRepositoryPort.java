package app.service.outputports;

import app.domain.Cliente;

import java.util.Optional;

public interface ClienteRepositoryPort {

    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(String idCliente);
}
