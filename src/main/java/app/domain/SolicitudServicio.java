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
}
