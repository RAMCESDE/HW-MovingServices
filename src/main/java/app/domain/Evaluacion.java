package app.domain;

import app.domain.exceptions.DatoInvalidoException;

import java.time.LocalDateTime;

public record Evaluacion(String idEvaluacion, String idReserva, int puntuacionServicio, int puntuacionPersonal,
                         String comentario, LocalDateTime fecha) {

    public Evaluacion {
        if (puntuacionServicio < 1 || puntuacionServicio > 5 || puntuacionPersonal < 1 || puntuacionPersonal > 5) {
            throw new DatoInvalidoException("La calificación debe estar entre 1 y 5 estrellas");
        }
    }
}
