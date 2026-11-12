package app.domain.enums;

/**
 * Ciclo de vida del servicio. Cada estado interno se traduce a la etapa simple
 * que ve el cliente (US-16): Pendiente, En Trayecto o Finalizada.
 */
public enum EstadoReserva {

    PENDIENTE_ANTICIPO("Pendiente de anticipo", "Pendiente"),
    RESERVADO("Reservado", "Pendiente"),
    CONFIRMADO("Confirmado", "Pendiente"),
    EN_DESPLAZAMIENTO("En desplazamiento", "En Trayecto"),
    EN_ORIGEN("En origen", "En Trayecto"),
    EN_DESTINO("En destino", "En Trayecto"),
    FINALIZADO("Finalizado", "Finalizada");

    private final String etiqueta;
    private final String etapaCliente;

    EstadoReserva(String etiqueta, String etapaCliente) {
        this.etiqueta = etiqueta;
        this.etapaCliente = etapaCliente;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getEtapaCliente() {
        return etapaCliente;
    }

    /** Un servicio ocupa vehículo y personal desde que se confirma hasta que finaliza. */
    public boolean ocupaRecursos() {
        return this == CONFIRMADO || this == EN_DESPLAZAMIENTO || this == EN_ORIGEN || this == EN_DESTINO;
    }
}
