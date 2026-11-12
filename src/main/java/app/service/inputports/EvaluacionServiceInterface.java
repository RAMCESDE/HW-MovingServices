package app.service.inputports;

import app.domain.Evaluacion;
import app.domain.Sesion;
import app.service.inputports.dto.MetricasCalidad;

public interface EvaluacionServiceInterface {

    Evaluacion calificar(Sesion sesion, String idReserva, int puntuacionServicio, int puntuacionPersonal,
                         String comentario);

    MetricasCalidad consultarMetricas(Sesion sesion);
}
