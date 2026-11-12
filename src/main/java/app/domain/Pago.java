package app.domain;

import app.domain.enums.EstadoPago;
import app.domain.enums.MetodoPago;
import app.domain.enums.TipoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pago {

    private String idPago;
    private String idReserva;
    private BigDecimal monto;
    private TipoPago tipoPago;
    private MetodoPago metodoPago;
    private EstadoPago estadoPago;
    private String referencia;
    private LocalDateTime fecha;

    public Pago() {
    }

    public Pago(String idPago, String idReserva, BigDecimal monto, TipoPago tipoPago, MetodoPago metodoPago,
                EstadoPago estadoPago, String referencia, LocalDateTime fecha) {
        this.idPago = idPago;
        this.idReserva = idReserva;
        this.monto = monto;
        this.tipoPago = tipoPago;
        this.metodoPago = metodoPago;
        this.estadoPago = estadoPago;
        this.referencia = referencia;
        this.fecha = fecha;
    }

    public void aprobar(String referencia, LocalDateTime fecha) {
        this.estadoPago = EstadoPago.APROBADO;
        this.referencia = referencia;
        this.fecha = fecha;
    }

    public boolean aprobado() {
        return estadoPago == EstadoPago.APROBADO;
    }

    public String getIdPago() {
        return idPago;
    }

    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public TipoPago getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(TipoPago tipoPago) {
        this.tipoPago = tipoPago;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public EstadoPago getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(EstadoPago estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
