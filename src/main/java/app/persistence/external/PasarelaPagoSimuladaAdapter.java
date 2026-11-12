package app.persistence.external;

import app.domain.enums.MetodoPago;
import app.service.helpers.IdGenerator;
import app.service.outputports.PasarelaPagoPort;

import java.math.BigDecimal;

/**
 * Pasarela de pruebas: no realiza cobros reales. El token "tok_rechazado" simula fondos insuficientes.
 * Una pasarela real (Wompi, PayU o Stripe) se conecta implementando PasarelaPagoPort.
 */
public class PasarelaPagoSimuladaAdapter implements PasarelaPagoPort {

    public static final String TOKEN_RECHAZADO = "tok_rechazado";

    @Override
    public ResultadoCobro cobrar(String idOperacion, BigDecimal monto, MetodoPago metodo, String tokenPago) {
        if (TOKEN_RECHAZADO.equals(tokenPago)) {
            return new ResultadoCobro(false, null, "fondos insuficientes");
        }
        return new ResultadoCobro(true, IdGenerator.nuevo("TX"), null);
    }
}
