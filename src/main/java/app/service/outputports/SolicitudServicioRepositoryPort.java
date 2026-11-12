package app.service.outputports;

import app.domain.SolicitudServicio;

import java.util.List;
import java.util.Optional;

public interface SolicitudServicioRepositoryPort {

    SolicitudServicio guardar(SolicitudServicio solicitud);

    Optional<SolicitudServicio> buscarPorId(String idSolicitud);

    List<SolicitudServicio> listarPorCliente(String idCliente);
}
