package app.persistence.external;

import app.domain.Ubicacion;
import app.domain.enums.Municipio;
import app.service.outputports.DistanciaPort;

/**
 * Estima los kilómetros entre municipios con la distancia en línea recta y un factor de vía.
 * Un servicio de mapas se puede conectar después implementando DistanciaPort.
 */
public class DistanciaMunicipiosAdapter implements DistanciaPort {

    private static final double RADIO_TIERRA_KM = 6371;
    private static final double FACTOR_VIA = 1.35;
    private static final double KM_MISMO_MUNICIPIO = 6;

    @Override
    public double calcularKm(Ubicacion origen, Ubicacion destino) {
        Municipio a = origen.municipio();
        Municipio b = destino.municipio();
        if (a == b) {
            return KM_MISMO_MUNICIPIO;
        }
        double dLat = Math.toRadians(b.getLatitud() - a.getLatitud());
        double dLon = Math.toRadians(b.getLongitud() - a.getLongitud());
        double h = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(Math.toRadians(a.getLatitud()))
                * Math.cos(Math.toRadians(b.getLatitud())) * Math.pow(Math.sin(dLon / 2), 2);
        double km = 2 * RADIO_TIERRA_KM * Math.asin(Math.sqrt(h)) * FACTOR_VIA;
        return Math.round(km * 10) / 10.0;
    }
}
