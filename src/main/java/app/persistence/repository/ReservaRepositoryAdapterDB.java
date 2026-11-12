package app.persistence.repository;

import app.domain.Reserva;
import app.domain.enums.EstadoReserva;
import app.persistence.dbconnection.JdbcTemplate;
import app.persistence.mappers.ReservaMapper;
import app.service.outputports.ReservaRepositoryPort;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReservaRepositoryAdapterDB implements ReservaRepositoryPort {

    private final JdbcTemplate jdbc;

    public ReservaRepositoryAdapterDB(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final ReservaMapper MAPPER = new ReservaMapper();
    private static final String ORDEN = " ORDER BY fecha_servicio";

    @Override
    public Reserva guardar(Reserva r) {
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("id_reserva", r.getIdReserva());
        f.put("id_solicitud", r.getIdSolicitud());
        f.put("id_cliente", r.getIdCliente());
        f.put("fecha_reserva", r.getFechaReserva());
        f.put("fecha_servicio", r.getFechaServicio());
        f.put("fecha_fin", r.getFechaFin());
        f.put("duracion_minutos", r.getDuracionMinutos());
        f.put("total", r.getTotal());
        f.put("estado", r.getEstado());
        f.put("placa_vehiculo", r.getPlacaVehiculo());
        f.put("id_conductor", r.getIdConductor());
        jdbc.upsert("reserva", f, "id_reserva");
        jdbc.update("DELETE FROM reserva_auxiliar WHERE id_reserva = ?", r.getIdReserva());
        for (String idEmpleado : r.getIdsAuxiliares()) {
            jdbc.update("INSERT INTO reserva_auxiliar (id_reserva, id_empleado) VALUES (?, ?)", r.getIdReserva(), idEmpleado);
        }
        return r;
    }

    @Override
    public Optional<Reserva> buscarPorId(String idReserva) {
        return jdbc.queryOne("SELECT * FROM reserva WHERE id_reserva = ?", MAPPER, idReserva).map(this::conAuxiliares);
    }

    @Override
    public List<Reserva> listarPorCliente(String idCliente) {
        return conAuxiliares(jdbc.query("SELECT * FROM reserva WHERE id_cliente = ?" + ORDEN, MAPPER, idCliente));
    }

    @Override
    public List<Reserva> listarPorEstado(EstadoReserva estado) {
        return conAuxiliares(jdbc.query("SELECT * FROM reserva WHERE estado = ?" + ORDEN, MAPPER, estado));
    }

    @Override
    public List<Reserva> listarPorEmpleado(String idEmpleado) {
        return conAuxiliares(jdbc.query("SELECT * FROM reserva WHERE id_conductor = ? OR id_reserva IN "
                + "(SELECT id_reserva FROM reserva_auxiliar WHERE id_empleado = ?)" + ORDEN, MAPPER, idEmpleado, idEmpleado));
    }

    @Override
    public List<Reserva> listarEntre(LocalDateTime desde, LocalDateTime hasta) {
        return conAuxiliares(jdbc.query("SELECT * FROM reserva WHERE fecha_servicio < ? AND fecha_fin > ?" + ORDEN,
                MAPPER, hasta, desde));
    }

    private List<Reserva> conAuxiliares(List<Reserva> reservas) {
        return reservas.stream().map(this::conAuxiliares).toList();
    }

    private Reserva conAuxiliares(Reserva r) {
        r.setIdsAuxiliares(jdbc.query("SELECT id_empleado FROM reserva_auxiliar WHERE id_reserva = ?",
                rs -> rs.getString(1), r.getIdReserva()));
        return r;
    }
}
