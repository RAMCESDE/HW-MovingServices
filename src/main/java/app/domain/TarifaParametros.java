package app.domain;

import app.domain.enums.TipoVehiculo;

import java.math.BigDecimal;
import java.util.Map;

/** Valores del algoritmo de precios; se configuran sin tocar el código. */
public record TarifaParametros(Map<TipoVehiculo, BigDecimal> tarifaBase, Map<TipoVehiculo, BigDecimal> valorKm,
                               Map<TipoVehiculo, Double> capacidadM3, BigDecimal valorPiso, BigDecimal valorAyudante,
                               double factorAcarreo) {
}
