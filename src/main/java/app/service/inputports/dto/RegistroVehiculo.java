package app.service.inputports.dto;

import app.domain.enums.TipoVehiculo;

import java.time.LocalDate;

public record RegistroVehiculo(String placa, TipoVehiculo tipo, String modelo, double capacidadCargaKg,
                               double capacidadVolumenM3, LocalDate vencimientoSoat,
                               LocalDate vencimientoTecnomecanica) {
}
