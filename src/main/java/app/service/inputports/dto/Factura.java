package app.service.inputports.dto;

import app.domain.Pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Factura(String numero, String idReserva, String cliente, BigDecimal total, List<Pago> pagos,
                      LocalDateTime fecha) {
}
