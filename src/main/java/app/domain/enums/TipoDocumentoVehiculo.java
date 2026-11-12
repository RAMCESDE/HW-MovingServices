package app.domain.enums;

public enum TipoDocumentoVehiculo {

    SOAT("SOAT"), TECNOMECANICA("Tecnomecánica");

    private final String etiqueta;

    TipoDocumentoVehiculo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
