package app.domain.exceptions;

/** Un dato de entrada no cumple el formato o las reglas de validación. */
public class DatoInvalidoException extends RuntimeException {

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
