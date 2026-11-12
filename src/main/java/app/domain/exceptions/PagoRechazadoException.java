package app.domain.exceptions;

/** La pasarela de pagos rechazó la transacción. */
public class PagoRechazadoException extends RuntimeException {

    public PagoRechazadoException(String mensaje) {
        super(mensaje);
    }
}
