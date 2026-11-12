package app.domain.enums;

public enum EstadoEmpleado {

    ACTIVO("Activo"), NO_DISPONIBLE("No disponible"), INACTIVO("Inactivo");

    private final String etiqueta;

    EstadoEmpleado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
