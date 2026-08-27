package app.domain;

public class SolicitudServicio {
    private String idSolicitud;
    private String tipoServicio;
    private String estado;
    private String fecha;

    public SolicitudServicio(){}

    public SolicitudServicio(String idSolicitud, String tipoServicio, String estado, String fecha) {
        this.idSolicitud = idSolicitud;
        this.tipoServicio = tipoServicio;
        this.estado = estado;
        this.fecha = fecha;
    }

    public void registrarSolicitud(){}

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(String idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
