package app.persistence.repository;

import app.domain.Reserva;
import app.domain.enums.EstadoReserva;
import app.service.outputports.ReservaRepositoryPort;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class ReservaRepositoryAdapter implements ReservaRepositoryPort {

    private final Map<String, Reserva> datos = new LinkedHashMap<>();

    @Override
    public Reserva guardar(Reserva reserva) {
        datos.put(reserva.getIdReserva(), reserva);
        return reserva;
    }

    @Override
    public Optional<Reserva> buscarPorId(String idReserva) {
        return Optional.ofNullable(datos.get(idReserva));
    }

    @Override
    public List<Reserva> listarPorCliente(String idCliente) {
        return filtrar(r -> r.getIdCliente().equals(idCliente));
    }

    @Override
    public List<Reserva> listarPorEstado(EstadoReserva estado) {
        return filtrar(r -> r.getEstado() == estado);
    }

    @Override
    public List<Reserva> listarPorEmpleado(String idEmpleado) {
        return filtrar(r -> r.participa(idEmpleado));
    }

    @Override
    public List<Reserva> listarEntre(LocalDateTime desde, LocalDateTime hasta) {
        return filtrar(r -> r.seTraslapaCon(desde, hasta));
    }

    private List<Reserva> filtrar(Predicate<Reserva> condicion) {
        return datos.values().stream().filter(condicion)
                .sorted(Comparator.comparing(Reserva::getFechaServicio)).toList();
    }
}
