package app.service.inputports;

import app.domain.AlertaDocumento;
import app.domain.Sesion;
import app.domain.Vehiculo;

import java.time.LocalDate;
import java.util.List;

public interface DocumentoVehiculoServiceInterface {

    Vehiculo actualizarDocumentos(Sesion sesion, String placa, LocalDate vencimientoSoat,
                                  LocalDate vencimientoTecnomecanica);

    /** Inhabilita los vehículos con documentos vencidos y devuelve las alertas vigentes. */
    List<AlertaDocumento> revisarDocumentos(Sesion sesion);
}
