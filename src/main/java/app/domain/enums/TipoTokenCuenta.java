package app.domain.enums;

public enum TipoTokenCuenta {

    VERIFICACION_CORREO("Verificación de correo"), RECUPERACION_CONTRASENA("Recuperación de contraseña");

    private final String etiqueta;

    TipoTokenCuenta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
