package app.domain.enums;

public enum EstadoSolicitud {

    COTIZADA("Cotizada"), BORRADOR("Borrador"), ACEPTADA("Aceptada"), RECHAZADA("Rechazada"), RESERVADA("Reservada");

    private final String etiqueta;

    EstadoSolicitud(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
