package app.service.inputports.dto;

import java.util.List;

public record MetricasCalidad(int totalEvaluaciones, double promedioServicio, double promedioPersonal,
                              List<CalificacionEmpleado> empleados) {

    public record CalificacionEmpleado(String idEmpleado, String nombre, double promedio, int evaluaciones) {
    }
}
