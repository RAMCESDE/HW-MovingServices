package app.domain;

import app.domain.enums.TipoTokenCuenta;

import java.time.LocalDateTime;

/** Token de un solo uso; solo se almacena su hash, nunca el valor enviado al correo. */
public class TokenCuenta {

    private String hashToken;
    private String idCuenta;
    private TipoTokenCuenta tipo;
    private LocalDateTime expira;
    private boolean usado;

    public TokenCuenta() {
    }

    public TokenCuenta(String hashToken, String idCuenta, TipoTokenCuenta tipo, LocalDateTime expira) {
        this.hashToken = hashToken;
        this.idCuenta = idCuenta;
        this.tipo = tipo;
        this.expira = expira;
    }

    public boolean expirado(LocalDateTime ahora) {
        return !ahora.isBefore(expira);
    }

    public String getHashToken() {
        return hashToken;
    }

    public void setHashToken(String hashToken) {
        this.hashToken = hashToken;
    }

    public String getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public TipoTokenCuenta getTipo() {
        return tipo;
    }

    public void setTipo(TipoTokenCuenta tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getExpira() {
        return expira;
    }

    public void setExpira(LocalDateTime expira) {
        this.expira = expira;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }
}
