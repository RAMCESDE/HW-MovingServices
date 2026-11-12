package app.persistence.repository;

import app.domain.Evaluacion;
import app.persistence.dbconnection.JdbcTemplate;
import app.persistence.mappers.EvaluacionMapper;
import app.service.outputports.EvaluacionRepositoryPort;

import java.util.List;
import java.util.Optional;

public class EvaluacionRepositoryAdapterDB implements EvaluacionRepositoryPort {

    private final JdbcTemplate jdbc;

    public EvaluacionRepositoryAdapterDB(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final EvaluacionMapper MAPPER = new EvaluacionMapper();

    @Override
    public Evaluacion guardar(Evaluacion e) {
        jdbc.update("INSERT INTO evaluacion (id_evaluacion, id_reserva, puntuacion_servicio, puntuacion_personal, "
                + "comentario, fecha) VALUES (?, ?, ?, ?, ?, ?)", e.idEvaluacion(), e.idReserva(),
                e.puntuacionServicio(), e.puntuacionPersonal(), e.comentario(), e.fecha());
        return e;
    }

    @Override
    public Optional<Evaluacion> buscarPorReserva(String idReserva) {
        return jdbc.queryOne("SELECT * FROM evaluacion WHERE id_reserva = ?", MAPPER, idReserva);
    }

    @Override
    public List<Evaluacion> listar() {
        return jdbc.query("SELECT * FROM evaluacion", MAPPER);
    }
}
