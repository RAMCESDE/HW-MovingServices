package app.persistence.repository;

import app.domain.EventoServicio;
import app.service.outputports.EventoServicioRepositoryPort;

import java.util.ArrayList;
import java.util.List;

public class EventoServicioRepositoryAdapter implements EventoServicioRepositoryPort {

    private final List<EventoServicio> datos = new ArrayList<>();

    @Override
    public EventoServicio guardar(EventoServicio evento) {
        datos.add(evento);
        return evento;
    }

    @Override
    public List<EventoServicio> listarPorReserva(String idReserva) {
        return datos.stream().filter(e -> e.idReserva().equals(idReserva)).toList();
    }
}
