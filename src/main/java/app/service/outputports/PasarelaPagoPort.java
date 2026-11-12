package app.service.outputports;

import app.domain.enums.MetodoPago;

import java.math.BigDecimal;

public interface PasarelaPagoPort {

    ResultadoCobro cobrar(String idOperacion, BigDecimal monto, MetodoPago metodo, String tokenPago);

    record ResultadoCobro(boolean aprobado, String referencia, String motivo) {
    }
}
