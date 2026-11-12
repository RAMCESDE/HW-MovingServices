package app.domain.enums;

public enum TipoCarga {

    LIVIANA("Liviana", 1.0),
    MEDIA("Media", 1.3),
    PESADA("Pesada", 1.6);

    private final String etiqueta;
    private final double factorPisos;

    TipoCarga(String etiqueta, double factorPisos) {
        this.etiqueta = etiqueta;
        this.factorPisos = factorPisos;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public double getFactorPisos() {
        return factorPisos;
    }
}
