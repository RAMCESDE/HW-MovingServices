package app.service.inputports;

import app.domain.Cliente;
import app.domain.Sesion;

public interface ClienteServiceInterface {

    Cliente obtenerPerfil(Sesion sesion);

    /** Los valores nulos conservan el dato actual. */
    Cliente actualizarPerfil(Sesion sesion, String nombre, String telefono, String direccion);
}
