package app.domain;

import app.domain.enums.Municipio;

public record Ubicacion(Municipio municipio, String direccion, int piso, boolean tieneAscensor) {

    /** Pisos que se deben subir o bajar por escalera en este punto. */
    public int pisosPorEscalera() {
        return tieneAscensor ? 0 : Math.max(piso - 1, 0);
    }
}
