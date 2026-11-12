package app.domain.enums;

public enum TipoServicio {

    ACARREO("Acarreo"), MUDANZA("Mudanza");

    private final String etiqueta;

    TipoServicio(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
