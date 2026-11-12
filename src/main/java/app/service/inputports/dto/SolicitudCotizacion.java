package app.service.inputports.dto;

import app.domain.Ubicacion;
import app.domain.enums.Enser;
import app.domain.enums.TipoCarga;
import app.domain.enums.TipoServicio;

import java.time.LocalDate;
import java.util.Map;

/** Se indica el volumen en m³ o la lista de enseres; si llega la lista, el volumen se calcula con ella. */
public record SolicitudCotizacion(TipoServicio tipoServicio, LocalDate fecha, Ubicacion origen, Ubicacion destino,
                                  double volumenM3, Map<Enser, Integer> enseres, TipoCarga tipoCarga, int ayudantes) {
}
