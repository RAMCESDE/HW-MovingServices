package app.service.outputports;

import app.domain.Ubicacion;

public interface DistanciaPort {

    double calcularKm(Ubicacion origen, Ubicacion destino);
}
