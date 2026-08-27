package app.domain;

public class Pago {

    private String idPago;
    private Double monto;
    private String tipoPago;
    private String metodoPago;
    private String estadoPago;

    public boolean procesarPago(){}
    public String generarComprobante(){}


    public Pago(){}

    public Pago(String idPago, Double monto, String tipoPago, String metodoPago, String estadoPago) {
        this.idPago = idPago;
        this.monto = monto;
        this.tipoPago = tipoPago;
        this.metodoPago = metodoPago;
        this.estadoPago = estadoPago;
    }


    public String getIdPago() {
        return idPago;
    }

    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }
}
