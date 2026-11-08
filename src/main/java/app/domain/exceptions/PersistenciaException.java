package app.domain.exceptions;

/** Falló el acceso al almacenamiento de datos. */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }
}
