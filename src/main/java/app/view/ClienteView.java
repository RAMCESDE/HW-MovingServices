package app.view;

import app.domain.Cliente;
import app.domain.Sesion;
import app.service.inputports.ClienteServiceInterface;
import app.view.helpers.EntradaConsola;
import app.view.helpers.Formato;

public class ClienteView {

    private final ClienteServiceInterface servicio;

    public ClienteView(ClienteServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void verPerfil(Sesion sesion) {
        mostrar(servicio.obtenerPerfil(sesion));
    }

    public void editarPerfil(Sesion sesion) {
        System.out.println("Deje el campo vacío para conservar el dato actual");
        String nombre = EntradaConsola.textoOpcional("Nombre");
        String telefono = EntradaConsola.textoOpcional("Teléfono");
        String direccion = EntradaConsola.textoOpcional("Dirección");
        mostrar(servicio.actualizarPerfil(sesion, nombre, telefono, direccion));
        System.out.println("Perfil actualizado.");
    }

    private void mostrar(Cliente cliente) {
        System.out.println("Correo: " + cliente.getEmail() + "\nNombre: " + Formato.texto(cliente.getNombre())
                + "\nTeléfono: " + Formato.texto(cliente.getTelefono())
                + "\nDirección: " + Formato.texto(cliente.getDireccion()));
    }
}
