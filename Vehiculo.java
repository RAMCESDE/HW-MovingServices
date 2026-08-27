package app.domain;

public class Vehiculo {

    private String placa;
    private String modelo;
    private Double capacidadCargaKg;
    private Boolean disponible;


    public Vehiculo()C}

    public Vehiculo(String placaC, String modelo, Double capacidadCargaKg, Boolean disponible) {
        this.placa = placaC;
        this.modelo = modelo;
        this.capacidadCargaKg = capacidadCargaKg;
        this.disponible = disponible;
    }

    public Boolean verificarDisponibilidad(){}

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Double getCapacidadCargaKg() {
        return capacidadCargaKg;
    }

    public void setCapacidadCargaKg(Double capacidadCargaKg) {
        this.capacidadCargaKg = capacidadCargaKg;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }
}
