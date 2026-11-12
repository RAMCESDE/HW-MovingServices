package app.service.inputports.dto;

import app.domain.Empleado;
import app.domain.Vehiculo;

import java.util.List;

public record RecursosDisponibles(List<Vehiculo> vehiculos, List<Empleado> conductores, List<Empleado> auxiliares,
                                  int auxiliaresRequeridos) {
}
