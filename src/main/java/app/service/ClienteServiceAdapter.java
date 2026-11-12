package app.service;

import app.domain.Cliente;
import app.domain.Sesion;
import app.domain.enums.Rol;
import app.domain.exceptions.RecursoNoEncontradoException;
import app.service.helpers.ControlAcceso;
import app.service.inputports.ClienteServiceInterface;
import app.service.outputports.ClienteRepositoryPort;
import app.service.validators.DatosValidator;

public class ClienteServiceAdapter implements ClienteServiceInterface {

    private final ClienteRepositoryPort clientes;

    public ClienteServiceAdapter(ClienteRepositoryPort clientes) {
        this.clientes = clientes;
    }

    @Override
    public Cliente obtenerPerfil(Sesion sesion) {
        ControlAcceso.exigirRol(sesion, Rol.CLIENTE);
        return clientes.buscarPorId(sesion.idPerfil())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el perfil del cliente"));
    }

    @Override
    public Cliente actualizarPerfil(Sesion sesion, String nombre, String telefono, String direccion) {
        Cliente cliente = obtenerPerfil(sesion);
        cliente.actualizarPerfil(nombre == null ? null : DatosValidator.requerido(nombre, "nombre"),
                telefono == null ? null : DatosValidator.telefono(telefono),
                direccion == null ? null : DatosValidator.requerido(direccion, "dirección"));
        return clientes.guardar(cliente);
    }
}
