package app.service.outputports;

import app.domain.Evaluacion;

import java.util.List;
import java.util.Optional;

public interface EvaluacionRepositoryPort {

    Evaluacion guardar(Evaluacion evaluacion);

    Optional<Evaluacion> buscarPorReserva(String idReserva);

    List<Evaluacion> listar();
}
