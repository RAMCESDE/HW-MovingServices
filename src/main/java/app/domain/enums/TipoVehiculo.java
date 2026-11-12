package app.domain.enums;

public enum TipoVehiculo {

    MOTOCARRO("Motocarro"), LUV("LUV"), NPR("NPR");

    private final String etiqueta;

    TipoVehiculo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
