package app.domain.enums;

public enum EstadoDocumento {

    VIGENTE("Vigente"), PROXIMO_A_VENCER("Documento Próximo a Vencer"), VENCIDO("Vencido");

    private final String etiqueta;

    EstadoDocumento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
