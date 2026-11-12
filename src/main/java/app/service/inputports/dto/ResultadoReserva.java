package app.service.inputports.dto;

import app.domain.Pago;
import app.domain.Reserva;

public record ResultadoReserva(Reserva reserva, Pago pago, String comprobante) {
}
