package app.service.outputports;

import app.domain.Reserva;
import app.domain.enums.EstadoReserva;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepositoryPort {

    Reserva guardar(Reserva reserva);

    Optional<Reserva> buscarPorId(String idReserva);

    List<Reserva> listarPorCliente(String idCliente);

    List<Reserva> listarPorEstado(EstadoReserva estado);

    List<Reserva> listarPorEmpleado(String idEmpleado);

    /** Reservas cuyo horario se cruza con el intervalo indicado. */
    List<Reserva> listarEntre(LocalDateTime desde, LocalDateTime hasta);
}
