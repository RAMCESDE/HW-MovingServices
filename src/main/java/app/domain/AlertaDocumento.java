package app.domain;

import app.domain.enums.EstadoDocumento;
import app.domain.enums.TipoDocumentoVehiculo;

import java.time.LocalDate;

public record AlertaDocumento(String placa, TipoDocumentoVehiculo documento, LocalDate vencimiento,
                              long diasRestantes, EstadoDocumento estado) {
}
