package app.domain.enums;

public enum CargoEmpleado {

    CONDUCTOR("Conductor"), AUXILIAR_CARGA("Auxiliar de carga");

    private final String etiqueta;

    CargoEmpleado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
