package app.persistence.repository;

import app.domain.Pago;
import app.service.outputports.PagoRepositoryPort;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PagoRepositoryAdapter implements PagoRepositoryPort {

    private final Map<String, Pago> datos = new LinkedHashMap<>();

    @Override
    public Pago guardar(Pago pago) {
        datos.put(pago.getIdPago(), pago);
        return pago;
    }

    @Override
    public List<Pago> listarPorReserva(String idReserva) {
        return datos.values().stream().filter(p -> p.getIdReserva().equals(idReserva)).toList();
    }
}
