package app.service.inputports.dto;

import app.domain.enums.EstadoReserva;

import java.time.LocalDateTime;

public record ServicioAsignado(String idReserva, LocalDateTime inicio, EstadoReserva estado, String placa,
                               String origen, String destino) {
}
