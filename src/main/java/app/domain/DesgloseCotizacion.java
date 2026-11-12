package app.domain;

import app.domain.enums.TipoVehiculo;

import java.math.BigDecimal;

public record DesgloseCotizacion(BigDecimal tarifaBase, BigDecimal recargoDistancia, BigDecimal recargoPisos,
                                 BigDecimal recargoPersonal, BigDecimal total, TipoVehiculo vehiculoSugerido,
                                 double distanciaKm, int duracionMinutos) {
}
