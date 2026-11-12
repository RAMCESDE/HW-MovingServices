package app.domain.exceptions;

/** El usuario no tiene permiso para esta operación. */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
