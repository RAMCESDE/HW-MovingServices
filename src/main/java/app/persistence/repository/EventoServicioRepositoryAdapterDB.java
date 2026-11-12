package app.persistence.repository;

import app.domain.EventoServicio;
import app.persistence.dbconnection.JdbcTemplate;
import app.persistence.mappers.EventoServicioMapper;
import app.service.outputports.EventoServicioRepositoryPort;

import java.util.List;

public class EventoServicioRepositoryAdapterDB implements EventoServicioRepositoryPort {

    private final JdbcTemplate jdbc;

    public EventoServicioRepositoryAdapterDB(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final EventoServicioMapper MAPPER = new EventoServicioMapper();

    @Override
    public EventoServicio guardar(EventoServicio e) {
        jdbc.update("INSERT INTO evento_servicio (id_evento, id_reserva, estado, fecha_hora, id_cuenta_actor) "
                + "VALUES (?, ?, ?, ?, ?)", e.idEvento(), e.idReserva(), e.estado(), e.fechaHora(), e.idCuentaActor());
        return e;
    }

    @Override
    public List<EventoServicio> listarPorReserva(String idReserva) {
        return jdbc.query("SELECT * FROM evento_servicio WHERE id_reserva = ? ORDER BY fecha_hora", MAPPER, idReserva);
    }
}
