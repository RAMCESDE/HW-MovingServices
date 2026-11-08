package app.service.helpers;

import app.domain.Sesion;
import app.domain.enums.Rol;
import app.domain.exceptions.AccesoDenegadoException;

import java.util.Arrays;

/** Autorización por rol (US-03), aplicada en los casos de uso y no solo en los menús. */
public final class ControlAcceso {

    private ControlAcceso() {
    }

    public static void exigirRol(Sesion sesion, Rol... permitidos) {
        if (sesion == null || Arrays.stream(permitidos).noneMatch(rol -> rol == sesion.rol())) {
            throw new AccesoDenegadoException("Su usuario no tiene permiso para realizar esta operación");
        }
    }

    public static void exigirPropietario(Sesion sesion, String idCliente) {
        exigirRol(sesion, Rol.CLIENTE);
        if (!sesion.idPerfil().equals(idCliente)) {
            throw new AccesoDenegadoException("Este registro pertenece a otro cliente");
        }
    }
}
