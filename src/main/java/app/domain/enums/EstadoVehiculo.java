package app.domain.enums;

public enum EstadoVehiculo {

    DISPONIBLE("Disponible"), INHABILITADO_DOCUMENTACION("Inhabilitado por Documentación");

    private final String etiqueta;

    EstadoVehiculo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
