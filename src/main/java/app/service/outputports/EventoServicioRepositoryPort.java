package app.service.outputports;

import app.domain.EventoServicio;

import java.util.List;

public interface EventoServicioRepositoryPort {

    EventoServicio guardar(EventoServicio evento);

    List<EventoServicio> listarPorReserva(String idReserva);
}
