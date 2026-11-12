package app.service.inputports;

import app.domain.RestriccionPicoPlaca;
import app.domain.Sesion;
import app.service.inputports.dto.NuevaRestriccionPicoPlaca;

import java.time.LocalDateTime;
import java.util.List;

public interface PicoPlacaServiceInterface {

    RestriccionPicoPlaca crearRestriccion(Sesion sesion, NuevaRestriccionPicoPlaca datos);

    List<RestriccionPicoPlaca> listarRestricciones(Sesion sesion);

    void eliminarRestriccion(Sesion sesion, String id);

    boolean vehiculoRestringido(Sesion sesion, String placa, LocalDateTime inicio, LocalDateTime fin);
}
