package app.domain;

public class Reserva {
    private String idReserva;
    private String fechaReserva;
    private String fechaServicio;
    private String estado;

    public Reserva(){
    }


    public Reserva(String idReserva, String fechaReserva, String fechaServicio, String estado){
        this.idReserva= idReserva;
        this.fechaReserva= fechaReserva;
        this.fechaServicio= fechaServicio;
        this.estado= estado;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public String getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(String fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getFechaServicio() {
        return fechaServicio;
    }

    public void setFechaServicio(String fechaServicio) {
        this.fechaServicio = fechaServicio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }



}
