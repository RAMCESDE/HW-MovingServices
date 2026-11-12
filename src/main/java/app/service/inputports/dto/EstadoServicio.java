package app.service.inputports.dto;

import app.domain.EventoServicio;
import app.domain.enums.EstadoReserva;

import java.time.LocalDateTime;
import java.util.List;

public record EstadoServicio(String idReserva, String etapa, EstadoReserva detalle, LocalDateTime fechaServicio,
                             String placa, String conductor, List<EventoServicio> historial) {
}
