package app.service.inputports;

import app.domain.Empleado;
import app.domain.Sesion;
import app.domain.enums.EstadoEmpleado;
import app.service.inputports.dto.RegistroEmpleado;

import java.util.List;

public interface EmpleadoServiceInterface {

    Empleado registrarEmpleado(Sesion sesion, RegistroEmpleado datos);

    List<Empleado> listarEmpleados(Sesion sesion);

    /** Inactivo también bloquea el acceso; No disponible solo impide nuevas asignaciones. */
    Empleado cambiarEstado(Sesion sesion, String idEmpleado, EstadoEmpleado nuevoEstado);
}
