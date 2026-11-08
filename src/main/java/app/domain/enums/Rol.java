package app.domain.enums;

public enum Rol {

    CLIENTE("Cliente"), ADMIN("Administrador"), CONDUCTOR("Conductor"), AUXILIAR_CARGA("Auxiliar de carga");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
