package app.domain.enums;

public enum MetodoPago {

    EFECTIVO("Efectivo"), TARJETA("Tarjeta"), TRANSFERENCIA("Transferencia");

    private final String etiqueta;

    MetodoPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
