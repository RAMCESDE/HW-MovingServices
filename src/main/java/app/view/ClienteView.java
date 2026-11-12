package app.view;

import app.domain.Cliente;
import app.domain.Sesion;
import app.service.inputports.ClienteServiceInterface;
import app.view.helpers.EntradaConsola;
import app.view.helpers.Ficha;
import app.view.helpers.Pantalla;

public class ClienteView {

    private final ClienteServiceInterface servicio;

    public ClienteView(ClienteServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void verPerfil(Sesion sesion) {
        mostrar(servicio.obtenerPerfil(sesion));
    }

    public void editarPerfil(Sesion sesion) {
        Pantalla.subtitulo("Editar perfil");
        Pantalla.info("Deje el campo vacío para conservar el dato actual.");
        String nombre = EntradaConsola.textoOpcional("Nombre");
        String telefono = EntradaConsola.textoOpcional("Teléfono");
        String direccion = EntradaConsola.textoOpcional("Dirección");
        Cliente cliente = servicio.actualizarPerfil(sesion, nombre, telefono, direccion);
        System.out.println();
        Pantalla.exito("Perfil actualizado.");
        mostrar(cliente);
    }

    private void mostrar(Cliente cliente) {
        new Ficha("Mi perfil")
                .campo("Correo", cliente.getEmail())
                .campo("Nombre", cliente.getNombre())
                .campo("Teléfono", cliente.getTelefono())
                .campo("Dirección", cliente.getDireccion())
                .imprimir();
    }
}
