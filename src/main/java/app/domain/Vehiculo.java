package app.domain;

import app.domain.enums.EstadoDocumento;
import app.domain.enums.EstadoVehiculo;
import app.domain.enums.TipoDocumentoVehiculo;
import app.domain.enums.TipoVehiculo;
import app.domain.exceptions.ReglaNegocioException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Vehiculo {

    public static final int DIAS_ALERTA_DOCUMENTO = 15;

    private String placa;
    private TipoVehiculo tipo;
    private String modelo;
    private double capacidadCargaKg;
    private double capacidadVolumenM3;
    private LocalDate vencimientoSoat;
    private LocalDate vencimientoTecnomecanica;
    private EstadoVehiculo estado = EstadoVehiculo.DISPONIBLE;
    private List<String> idsConductores = new ArrayList<>();

    public Vehiculo() {
    }

    public Vehiculo(String placa, TipoVehiculo tipo, String modelo, double capacidadCargaKg,
                    double capacidadVolumenM3, LocalDate vencimientoSoat, LocalDate vencimientoTecnomecanica) {
        this.placa = placa;
        this.tipo = tipo;
        this.modelo = modelo;
        this.capacidadCargaKg = capacidadCargaKg;
        this.capacidadVolumenM3 = capacidadVolumenM3;
        this.vencimientoSoat = vencimientoSoat;
        this.vencimientoTecnomecanica = vencimientoTecnomecanica;
    }

    public boolean verificarDisponibilidad() {
        return estado == EstadoVehiculo.DISPONIBLE;
    }

    public LocalDate vencimientoDe(TipoDocumentoVehiculo documento) {
        return documento == TipoDocumentoVehiculo.SOAT ? vencimientoSoat : vencimientoTecnomecanica;
    }

    public EstadoDocumento estadoDocumento(TipoDocumentoVehiculo documento, LocalDate hoy) {
        long dias = diasParaVencer(documento, hoy);
        if (dias < 0) {
            return EstadoDocumento.VENCIDO;
        }
        return dias <= DIAS_ALERTA_DOCUMENTO ? EstadoDocumento.PROXIMO_A_VENCER : EstadoDocumento.VIGENTE;
    }

    public long diasParaVencer(TipoDocumentoVehiculo documento, LocalDate hoy) {
        return ChronoUnit.DAYS.between(hoy, vencimientoDe(documento));
    }

    public boolean documentosVigentesEn(LocalDate fecha) {
        return !vencimientoSoat.isBefore(fecha) && !vencimientoTecnomecanica.isBefore(fecha);
    }

    /** Inhabilita con documentos vencidos y rehabilita al renovarlos. */
    public void sincronizarEstadoConDocumentos(LocalDate hoy) {
        estado = documentosVigentesEn(hoy) ? EstadoVehiculo.DISPONIBLE : EstadoVehiculo.INHABILITADO_DOCUMENTACION;
    }

    public void asignarConductor(String idEmpleado) {
        if (idsConductores.contains(idEmpleado)) {
            throw new ReglaNegocioException("El conductor ya está asignado al vehículo " + placa);
        }
        idsConductores.add(idEmpleado);
    }

    public void retirarConductor(String idEmpleado) {
        if (!idsConductores.remove(idEmpleado)) {
            throw new ReglaNegocioException("El conductor no está asignado al vehículo " + placa);
        }
    }

    /** Último dígito numérico de la placa, usado para Pico y Placa. */
    public int ultimoDigitoPlaca() {
        for (int i = placa.length() - 1; i >= 0; i--) {
            if (Character.isDigit(placa.charAt(i))) {
                return Character.getNumericValue(placa.charAt(i));
            }
        }
        throw new ReglaNegocioException("La placa " + placa + " no contiene dígitos");
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVehiculo tipo) {
        this.tipo = tipo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getCapacidadCargaKg() {
        return capacidadCargaKg;
    }

    public void setCapacidadCargaKg(double capacidadCargaKg) {
        this.capacidadCargaKg = capacidadCargaKg;
    }

    public double getCapacidadVolumenM3() {
        return capacidadVolumenM3;
    }

    public void setCapacidadVolumenM3(double capacidadVolumenM3) {
        this.capacidadVolumenM3 = capacidadVolumenM3;
    }

    public LocalDate getVencimientoSoat() {
        return vencimientoSoat;
    }

    public void setVencimientoSoat(LocalDate vencimientoSoat) {
        this.vencimientoSoat = vencimientoSoat;
    }

    public LocalDate getVencimientoTecnomecanica() {
        return vencimientoTecnomecanica;
    }

    public void setVencimientoTecnomecanica(LocalDate vencimientoTecnomecanica) {
        this.vencimientoTecnomecanica = vencimientoTecnomecanica;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    public List<String> getIdsConductores() {
        return idsConductores;
    }

    public void setIdsConductores(List<String> idsConductores) {
        this.idsConductores = new ArrayList<>(idsConductores);
    }
}
