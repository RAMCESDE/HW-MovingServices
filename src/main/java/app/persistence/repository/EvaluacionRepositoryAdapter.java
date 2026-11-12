package app.persistence.repository;

import app.domain.Evaluacion;
import app.service.outputports.EvaluacionRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EvaluacionRepositoryAdapter implements EvaluacionRepositoryPort {

    private final List<Evaluacion> datos = new ArrayList<>();

    @Override
    public Evaluacion guardar(Evaluacion evaluacion) {
        datos.add(evaluacion);
        return evaluacion;
    }

    @Override
    public Optional<Evaluacion> buscarPorReserva(String idReserva) {
        return datos.stream().filter(e -> e.idReserva().equals(idReserva)).findFirst();
    }

    @Override
    public List<Evaluacion> listar() {
        return new ArrayList<>(datos);
    }
}
