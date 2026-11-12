package app.service.outputports;

import app.domain.Pago;

import java.util.List;

public interface PagoRepositoryPort {

    Pago guardar(Pago pago);

    List<Pago> listarPorReserva(String idReserva);
}
