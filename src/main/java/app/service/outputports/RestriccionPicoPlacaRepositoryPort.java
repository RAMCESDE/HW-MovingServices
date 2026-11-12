package app.service.outputports;

import app.domain.RestriccionPicoPlaca;

import java.util.List;

public interface RestriccionPicoPlacaRepositoryPort {

    RestriccionPicoPlaca guardar(RestriccionPicoPlaca restriccion);

    List<RestriccionPicoPlaca> listar();

    boolean eliminar(String id);
}
