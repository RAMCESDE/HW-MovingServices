package app.domain;

import app.domain.enums.Rol;

import java.time.LocalDateTime;

/** Credenciales de acceso, separadas de los datos de la persona (SRP). */
public class Cuenta {

    private String idCuenta;
    private String email;
    private String passwordHash;
    private Rol rol;
    private String idPerfil;
    private boolean activa = true;
    private boolean correoVerificado;
    private LocalDateTime fechaCreacion;

    public Cuenta() {
    }

    public Cuenta(String idCuenta, String email, String passwordHash, Rol rol, String idPerfil,
                  LocalDateTime fechaCreacion) {
        this.idCuenta = idCuenta;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.idPerfil = idPerfil;
        this.fechaCreacion = fechaCreacion;
    }

    public String getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(String idPerfil) {
        this.idPerfil = idPerfil;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public boolean isCorreoVerificado() {
        return correoVerificado;
    }

    public void setCorreoVerificado(boolean correoVerificado) {
        this.correoVerificado = correoVerificado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
