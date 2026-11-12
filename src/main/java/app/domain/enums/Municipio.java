package app.domain.enums;

/** Zona de operación: Valle de Aburrá y alrededores, con coordenadas aproximadas. */
public enum Municipio {

    MEDELLIN("Medellín", 6.2442, -75.5812),
    BELLO("Bello", 6.3373, -75.5579),
    ITAGUI("Itagüí", 6.1719, -75.6114),
    ENVIGADO("Envigado", 6.1759, -75.5917),
    SABANETA("Sabaneta", 6.1515, -75.6166),
    LA_ESTRELLA("La Estrella", 6.1576, -75.6430),
    CALDAS("Caldas", 6.0911, -75.6357),
    COPACABANA("Copacabana", 6.3463, -75.5089),
    GIRARDOTA("Girardota", 6.3775, -75.4440),
    BARBOSA("Barbosa", 6.4390, -75.3307),
    GUARNE("Guarne", 6.2798, -75.4431),
    RIONEGRO("Rionegro", 6.1551, -75.3737),
    MARINILLA("Marinilla", 6.1738, -75.3390);

    private final String etiqueta;
    private final double latitud;
    private final double longitud;

    Municipio(String etiqueta, double latitud, double longitud) {
        this.etiqueta = etiqueta;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }
}
