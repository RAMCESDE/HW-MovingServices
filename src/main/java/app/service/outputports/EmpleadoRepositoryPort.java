package app.service.outputports;

import app.domain.Empleado;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepositoryPort {

    Empleado guardar(Empleado empleado);

    Optional<Empleado> buscarPorId(String idEmpleado);

    Optional<Empleado> buscarPorCedula(String cedula);

    List<Empleado> listar();
}
