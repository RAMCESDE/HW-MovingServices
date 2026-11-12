package app.persistence.repository;

import app.domain.Empleado;
import app.service.outputports.EmpleadoRepositoryPort;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmpleadoRepositoryAdapter implements EmpleadoRepositoryPort {

    private final Map<String, Empleado> datos = new LinkedHashMap<>();

    @Override
    public Empleado guardar(Empleado empleado) {
        datos.put(empleado.getIdEmpleado(), empleado);
        return empleado;
    }

    @Override
    public Optional<Empleado> buscarPorId(String idEmpleado) {
        return Optional.ofNullable(datos.get(idEmpleado));
    }

    @Override
    public Optional<Empleado> buscarPorCedula(String cedula) {
        return datos.values().stream().filter(e -> cedula.equals(e.getCedula())).findFirst();
    }

    @Override
    public List<Empleado> listar() {
        return new ArrayList<>(datos.values());
    }
}
