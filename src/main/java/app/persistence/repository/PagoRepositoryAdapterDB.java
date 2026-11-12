package app.persistence.repository;

import app.domain.Pago;
import app.persistence.dbconnection.JdbcTemplate;
import app.persistence.mappers.PagoMapper;
import app.service.outputports.PagoRepositoryPort;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PagoRepositoryAdapterDB implements PagoRepositoryPort {

    private final JdbcTemplate jdbc;

    public PagoRepositoryAdapterDB(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final PagoMapper MAPPER = new PagoMapper();

    @Override
    public Pago guardar(Pago p) {
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("id_pago", p.getIdPago());
        f.put("id_reserva", p.getIdReserva());
        f.put("monto", p.getMonto());
        f.put("tipo_pago", p.getTipoPago());
        f.put("metodo_pago", p.getMetodoPago());
        f.put("estado_pago", p.getEstadoPago());
        f.put("referencia", p.getReferencia());
        f.put("fecha", p.getFecha());
        jdbc.upsert("pago", f, "id_pago");
        return p;
    }

    @Override
    public List<Pago> listarPorReserva(String idReserva) {
        return jdbc.query("SELECT * FROM pago WHERE id_reserva = ? ORDER BY fecha", MAPPER, idReserva);
    }
}
