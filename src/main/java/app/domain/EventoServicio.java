package app.domain;

import app.domain.enums.EstadoReserva;

import java.time.LocalDateTime;

/** Registro de cada cambio de estado del servicio, para el seguimiento del cliente. */
public record EventoServicio(String idEvento, String idReserva, EstadoReserva estado, LocalDateTime fechaHora,
                             String idCuentaActor) {
}
