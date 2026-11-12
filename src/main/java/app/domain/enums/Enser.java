package app.domain.enums;

/** Catálogo de enseres con un volumen de referencia para estimar la carga. */
public enum Enser {

    CAJA("Caja mediana", 0.10),
    TELEVISOR("Televisor", 0.20),
    LAVADORA("Lavadora", 0.60),
    ESCRITORIO("Escritorio", 0.80),
    COLCHON("Colchón", 0.80),
    NEVERA("Nevera", 1.20),
    CAMA("Cama doble", 1.50),
    COMEDOR("Juego de comedor", 1.80),
    CLOSET("Clóset", 2.00),
    SOFA("Sofá", 2.00);

    private final String etiqueta;
    private final double volumenM3;

    Enser(String etiqueta, double volumenM3) {
        this.etiqueta = etiqueta;
        this.volumenM3 = volumenM3;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public double getVolumenM3() {
        return volumenM3;
    }
}
