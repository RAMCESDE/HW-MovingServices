package app.domain;

import app.domain.enums.Enser;
import app.domain.enums.EstadoSolicitud;
import app.domain.enums.TipoCarga;
import app.domain.enums.TipoServicio;
import app.domain.exceptions.ReglaNegocioException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

/** Solicitud de cotización del cliente junto con el precio calculado. */
public class SolicitudServicio {

    private String idSolicitud;
    private String idCliente;
    private TipoServicio tipoServicio;
    private EstadoSolicitud estado;
    private LocalDate fecha;
    private Ubicacion origen;
    private Ubicacion destino;
    private double volumenM3;
    private Map<Enser, Integer> enseres = new EnumMap<>(Enser.class);
    private TipoCarga tipoCarga;
    private int ayudantes;
    private DesgloseCotizacion cotizacion;
    private LocalDateTime fechaCreacion;

    public void guardarBorrador() {
        exigirEstado(EstadoSolicitud.COTIZADA);
        estado = EstadoSolicitud.BORRADOR;
    }

    public void aceptar() {
        exigirEstado(EstadoSolicitud.COTIZADA, EstadoSolicitud.BORRADOR);
        estado = EstadoSolicitud.ACEPTADA;
    }

    public void rechazar() {
        exigirEstado(EstadoSolicitud.COTIZADA, EstadoSolicitud.BORRADOR);
        estado = EstadoSolicitud.RECHAZADA;
    }

    public void marcarReservada() {
        exigirEstado(EstadoSolicitud.ACEPTADA);
        estado = EstadoSolicitud.RESERVADA;
    }

    private void exigirEstado(EstadoSolicitud... permitidos) {
        for (EstadoSolicitud permitido : permitidos) {
            if (estado == permitido) {
                return;
            }
        }
        throw new ReglaNegocioException("La cotización " + idSolicitud + " está en estado \""
                + estado.getEtiqueta() + "\" y no permite esta operación");
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

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Ubicacion getOrigen() {
        return origen;
    }

    public void setOrigen(Ubicacion origen) {
        this.origen = origen;
    }

    public Ubicacion getDestino() {
        return destino;
    }

    public void setDestino(Ubicacion destino) {
        this.destino = destino;
    }

    public double getVolumenM3() {
        return volumenM3;
    }

    public void setVolumenM3(double volumenM3) {
        this.volumenM3 = volumenM3;
    }

    public Map<Enser, Integer> getEnseres() {
        return enseres;
    }

    public void setEnseres(Map<Enser, Integer> enseres) {
        this.enseres = enseres.isEmpty() ? new EnumMap<>(Enser.class) : new EnumMap<>(enseres);
    }

    public TipoCarga getTipoCarga() {
        return tipoCarga;
    }

    public void setTipoCarga(TipoCarga tipoCarga) {
        this.tipoCarga = tipoCarga;
    }

    public int getAyudantes() {
        return ayudantes;
    }

    public void setAyudantes(int ayudantes) {
        this.ayudantes = ayudantes;
    }

    public DesgloseCotizacion getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(DesgloseCotizacion cotizacion) {
        this.cotizacion = cotizacion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
