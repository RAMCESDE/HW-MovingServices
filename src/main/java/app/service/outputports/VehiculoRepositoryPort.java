package app.service.outputports;

import app.domain.Vehiculo;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepositoryPort {

    Vehiculo guardar(Vehiculo vehiculo);

    Optional<Vehiculo> buscarPorPlaca(String placa);

    List<Vehiculo> listar();
}
