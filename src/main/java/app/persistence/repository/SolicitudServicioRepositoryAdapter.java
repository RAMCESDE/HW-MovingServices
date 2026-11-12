package app.persistence.repository;

import app.domain.SolicitudServicio;
import app.service.outputports.SolicitudServicioRepositoryPort;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SolicitudServicioRepositoryAdapter implements SolicitudServicioRepositoryPort {

    private final Map<String, SolicitudServicio> datos = new LinkedHashMap<>();

    @Override
    public SolicitudServicio guardar(SolicitudServicio solicitud) {
        datos.put(solicitud.getIdSolicitud(), solicitud);
        return solicitud;
    }

    @Override
    public Optional<SolicitudServicio> buscarPorId(String idSolicitud) {
        return Optional.ofNullable(datos.get(idSolicitud));
    }

    @Override
    public List<SolicitudServicio> listarPorCliente(String idCliente) {
        return datos.values().stream().filter(s -> s.getIdCliente().equals(idCliente)).toList();
    }
}
