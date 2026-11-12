package app.persistence.repository;

import app.domain.Vehiculo;
import app.service.outputports.VehiculoRepositoryPort;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VehiculoRepositoryAdapter implements VehiculoRepositoryPort {

    private final Map<String, Vehiculo> datos = new LinkedHashMap<>();

    @Override
    public Vehiculo guardar(Vehiculo vehiculo) {
        datos.put(vehiculo.getPlaca(), vehiculo);
        return vehiculo;
    }

    @Override
    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        return Optional.ofNullable(datos.get(placa));
    }

    @Override
    public List<Vehiculo> listar() {
        return new ArrayList<>(datos.values());
    }
}
