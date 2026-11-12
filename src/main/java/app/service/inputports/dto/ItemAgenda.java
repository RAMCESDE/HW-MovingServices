package app.service.inputports.dto;

import app.domain.enums.EstadoReserva;

import java.time.LocalDateTime;
import java.util.List;

public record ItemAgenda(String idReserva, LocalDateTime inicio, LocalDateTime fin, EstadoReserva estado, String placa,
                         String conductor, List<String> auxiliares, String origen, String destino) {
}
