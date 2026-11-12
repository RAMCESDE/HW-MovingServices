package app.domain;

import app.domain.enums.EstadoReserva;
import app.domain.exceptions.ReglaNegocioException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reserva {

    private String idReserva;
    private String idSolicitud;
    private String idCliente;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaServicio;
    private int duracionMinutos;
    private BigDecimal total;
    private EstadoReserva estado;
    private String placaVehiculo;
    private String idConductor;
    private List<String> idsAuxiliares = new ArrayList<>();

    public void confirmarAnticipo() {
        exigirEstado(EstadoReserva.PENDIENTE_ANTICIPO);
        estado = EstadoReserva.RESERVADO;
    }

    public void programar(LocalDateTime fechaServicio, String placaVehiculo, String idConductor,
                          List<String> idsAuxiliares) {
        exigirEstado(EstadoReserva.RESERVADO);
        this.fechaServicio = fechaServicio;
        this.placaVehiculo = placaVehiculo;
        this.idConductor = idConductor;
        this.idsAuxiliares = new ArrayList<>(idsAuxiliares);
        estado = EstadoReserva.CONFIRMADO;
    }

    /** El conductor avanza un hito a la vez: En desplazamiento, En origen y En destino. */
    public EstadoReserva avanzarHito() {
        estado = switch (estado) {
            case CONFIRMADO -> EstadoReserva.EN_DESPLAZAMIENTO;
            case EN_DESPLAZAMIENTO -> EstadoReserva.EN_ORIGEN;
            case EN_ORIGEN -> EstadoReserva.EN_DESTINO;
            default -> throw new ReglaNegocioException("El servicio en estado \"" + estado.getEtiqueta()
                    + "\" no tiene un siguiente hito de ruta");
        };
        return estado;
    }

    public void finalizar() {
        exigirEstado(EstadoReserva.EN_DESTINO);
        estado = EstadoReserva.FINALIZADO;
    }

    public LocalDateTime getFechaFin() {
        return fechaServicio.plusMinutes(duracionMinutos);
    }

    public boolean participa(String idEmpleado) {
        return idEmpleado.equals(idConductor) || idsAuxiliares.contains(idEmpleado);
    }

    public boolean seTraslapaCon(LocalDateTime inicio, LocalDateTime fin) {
        return fechaServicio.isBefore(fin) && getFechaFin().isAfter(inicio);
    }

    private void exigirEstado(EstadoReserva requerido) {
        if (estado != requerido) {
            throw new ReglaNegocioException("La reserva " + idReserva + " debe estar en estado \""
                    + requerido.getEtiqueta() + "\" y está en \"" + estado.getEtiqueta() + "\"");
        }
    }

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(String idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalDateTime getFechaServicio() {
        return fechaServicio;
    }

    public void setFechaServicio(LocalDateTime fechaServicio) {
        this.fechaServicio = fechaServicio;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }

    public String getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(String idConductor) {
        this.idConductor = idConductor;
    }

    public List<String> getIdsAuxiliares() {
        return idsAuxiliares;
    }

    public void setIdsAuxiliares(List<String> idsAuxiliares) {
        this.idsAuxiliares = new ArrayList<>(idsAuxiliares);
    }
}
