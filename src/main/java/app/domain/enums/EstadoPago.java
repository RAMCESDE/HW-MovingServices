package app.domain.enums;

public enum EstadoPago {

    PENDIENTE("Pendiente"), APROBADO("Aprobado");

    private final String etiqueta;

    EstadoPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
