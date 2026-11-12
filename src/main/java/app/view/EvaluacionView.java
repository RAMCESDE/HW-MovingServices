package app.view;

import app.domain.Sesion;
import app.service.inputports.EvaluacionServiceInterface;
import app.service.inputports.dto.MetricasCalidad;
import app.view.helpers.EntradaConsola;

public class EvaluacionView {

    private final EvaluacionServiceInterface servicio;

    public EvaluacionView(EvaluacionServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void calificar(Sesion sesion) {
        String id = EntradaConsola.texto("Id de la reserva");
        int puntuacionServicio = EntradaConsola.entero("Calificación del servicio (1 a 5)");
        int puntuacionPersonal = EntradaConsola.entero("Calificación del personal (1 a 5)");
        String comentario = EntradaConsola.textoOpcional("Comentario");
        servicio.calificar(sesion, id, puntuacionServicio, puntuacionPersonal, comentario);
        System.out.println("¡Gracias por su calificación!");
    }

    public void metricas(Sesion sesion) {
        MetricasCalidad m = servicio.consultarMetricas(sesion);
        System.out.printf("Evaluaciones: %d | Servicio: %.1f | Personal: %.1f%n", m.totalEvaluaciones(),
                m.promedioServicio(), m.promedioPersonal());
        for (MetricasCalidad.CalificacionEmpleado e : m.empleados()) {
            System.out.printf("  %s | %.1f (%d)%n", e.nombre(), e.promedio(), e.evaluaciones());
        }
    }
}
