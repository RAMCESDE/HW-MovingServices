package app.persistence.repository;

import app.domain.RestriccionPicoPlaca;
import app.service.outputports.RestriccionPicoPlacaRepositoryPort;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestriccionPicoPlacaRepositoryAdapter implements RestriccionPicoPlacaRepositoryPort {

    private final Map<String, RestriccionPicoPlaca> datos = new LinkedHashMap<>();

    @Override
    public RestriccionPicoPlaca guardar(RestriccionPicoPlaca restriccion) {
        datos.put(restriccion.id(), restriccion);
        return restriccion;
    }

    @Override
    public List<RestriccionPicoPlaca> listar() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public boolean eliminar(String id) {
        return datos.remove(id) != null;
    }
}
