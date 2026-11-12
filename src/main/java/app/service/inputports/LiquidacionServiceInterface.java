package app.service.inputports;

import app.domain.Sesion;
import app.domain.enums.MetodoPago;
import app.service.inputports.dto.Factura;

import java.math.BigDecimal;

public interface LiquidacionServiceInterface {

    BigDecimal consultarSaldo(Sesion sesion, String idReserva);

    Factura pagarSaldoDigital(Sesion sesion, String idReserva, MetodoPago metodo, String tokenPago);

    Factura registrarSaldoEfectivo(Sesion sesion, String idReserva);

    Factura consultarFactura(Sesion sesion, String idReserva);
}
