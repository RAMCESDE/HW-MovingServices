package app.domain;

import app.domain.enums.CargoEmpleado;
import app.domain.enums.EstadoEmpleado;

import java.time.LocalDate;

public class Empleado extends Persona {

    private String idEmpleado;
    private CargoEmpleado cargo;
    private String turno;
    private Integer edad;
    private String sexo;
    private String estadoCivil;
    private String eps;
    private String tipoLicencia;
    private LocalDate vencimientoLicencia;
    private String tallaDotacion;
    private EstadoEmpleado estado = EstadoEmpleado.ACTIVO;
    private int sumaCalificaciones;
    private int totalCalificaciones;

    public Empleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Empleado(String nombre, String cedula, String telefono, String email, String idEmpleado) {
        super(nombre, cedula, telefono, email);
        this.idEmpleado = idEmpleado;
    }

    public void cambiarEstado(EstadoEmpleado nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public boolean esAsignable() {
        return estado == EstadoEmpleado.ACTIVO;
    }

    public boolean licenciaVigenteEn(LocalDate fecha) {
        return vencimientoLicencia != null && !vencimientoLicencia.isBefore(fecha);
    }

    public void registrarCalificacion(int puntuacion) {
        sumaCalificaciones += puntuacion;
        totalCalificaciones++;
    }

    public double getCalificacionPromedio() {
        return totalCalificaciones == 0 ? 0 : (double) sumaCalificaciones / totalCalificaciones;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public CargoEmpleado getCargo() {
        return cargo;
    }

    public void setCargo(CargoEmpleado cargo) {
        this.cargo = cargo;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }

    public String getTipoLicencia() {
        return tipoLicencia;
    }

    public void setTipoLicencia(String tipoLicencia) {
        this.tipoLicencia = tipoLicencia;
    }

    public LocalDate getVencimientoLicencia() {
        return vencimientoLicencia;
    }

    public void setVencimientoLicencia(LocalDate vencimientoLicencia) {
        this.vencimientoLicencia = vencimientoLicencia;
    }

    public String getTallaDotacion() {
        return tallaDotacion;
    }

    public void setTallaDotacion(String tallaDotacion) {
        this.tallaDotacion = tallaDotacion;
    }

    public EstadoEmpleado getEstado() {
        return estado;
    }

    public void setEstado(EstadoEmpleado estado) {
        this.estado = estado;
    }

    public int getSumaCalificaciones() {
        return sumaCalificaciones;
    }

    public void setSumaCalificaciones(int sumaCalificaciones) {
        this.sumaCalificaciones = sumaCalificaciones;
    }

    public int getTotalCalificaciones() {
        return totalCalificaciones;
    }

    public void setTotalCalificaciones(int totalCalificaciones) {
        this.totalCalificaciones = totalCalificaciones;
    }
}
