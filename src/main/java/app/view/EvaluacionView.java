package app.view;

import app.domain.Sesion;
import app.service.inputports.EvaluacionServiceInterface;
import app.service.inputports.dto.MetricasCalidad;
import app.view.helpers.EntradaConsola;
import app.view.helpers.Ficha;
import app.view.helpers.Formato;
import app.view.helpers.Pantalla;
import app.view.helpers.Tabla;

public class EvaluacionView {

    private final EvaluacionServiceInterface servicio;

    public EvaluacionView(EvaluacionServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void calificar(Sesion sesion) {
        Pantalla.subtitulo("Calificar servicio");
        String id = EntradaConsola.texto("Id de la reserva");
        int puntuacionServicio = EntradaConsola.entero("Calificación del servicio (1 a 5)");
        int puntuacionPersonal = EntradaConsola.entero("Calificación del personal (1 a 5)");
        String comentario = EntradaConsola.textoOpcional("Comentario");
        servicio.calificar(sesion, id, puntuacionServicio, puntuacionPersonal, comentario);
        Pantalla.exito("¡Gracias por su calificación!");
    }

    public void metricas(Sesion sesion) {
        MetricasCalidad m = servicio.consultarMetricas(sesion);
        new Ficha("Métricas de calidad")
                .campo("Evaluaciones", m.totalEvaluaciones())
                .campo("Promedio del servicio", Formato.decimal(m.promedioServicio()) + " / 5")
                .campo("Promedio del personal", Formato.decimal(m.promedioPersonal()) + " / 5")
                .imprimir();
        Pantalla.subtitulo("Calificación por empleado");
        Tabla tabla = new Tabla("Empleado", "Promedio", "Evaluaciones");
        for (MetricasCalidad.CalificacionEmpleado e : m.empleados()) {
            tabla.fila(e.nombre(), Formato.decimal(e.promedio()) + " / 5", e.evaluaciones());
        }
        tabla.imprimir("Aún no hay empleados calificados.");
    }
}
