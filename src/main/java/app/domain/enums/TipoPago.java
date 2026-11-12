package app.domain.enums;

public enum TipoPago {

    ANTICIPO("Anticipo"), SALDO("Saldo");

    private final String etiqueta;

    TipoPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
