package app.view;

import app.domain.Sesion;
import app.domain.Vehiculo;
import app.domain.enums.TipoVehiculo;
import app.service.inputports.FlotaServiceInterface;
import app.service.inputports.dto.RegistroVehiculo;
import app.view.helpers.EntradaConsola;

import java.util.List;

public class FlotaView {

    private final FlotaServiceInterface servicio;

    public FlotaView(FlotaServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void registrar(Sesion sesion) {
        RegistroVehiculo datos = new RegistroVehiculo(EntradaConsola.texto("Placa"),
                EntradaConsola.opcion("Tipo de vehículo", TipoVehiculo.values(), TipoVehiculo::getEtiqueta),
                EntradaConsola.texto("Modelo"), EntradaConsola.decimal("Capacidad de carga (kg)"),
                EntradaConsola.decimal("Capacidad de volumen (m³)"), EntradaConsola.fecha("Vencimiento del SOAT"),
                EntradaConsola.fecha("Vencimiento de la tecnomecánica"));
        Vehiculo vehiculo = servicio.registrarVehiculo(sesion, datos);
        System.out.println("Vehículo " + vehiculo.getPlaca() + " registrado. Estado: " + vehiculo.getEstado().getEtiqueta());
    }

    public void listar(Sesion sesion) {
        List<Vehiculo> flota = servicio.listarVehiculos(sesion);
        if (flota.isEmpty()) {
            System.out.println("No hay vehículos registrados.");
        }
        for (Vehiculo v : flota) {
            System.out.println(v.getPlaca() + " | " + v.getTipo().getEtiqueta() + " | " + v.getModelo() + " | "
                    + v.getCapacidadCargaKg() + " kg | " + v.getCapacidadVolumenM3() + " m³ | SOAT " + v.getVencimientoSoat()
                    + " | Tecno " + v.getVencimientoTecnomecanica() + " | " + v.getEstado().getEtiqueta()
                    + " | Conductores: " + v.getIdsConductores());
        }
    }

    public void asignarConductor(Sesion sesion) {
        Vehiculo vehiculo = servicio.asignarConductor(sesion, EntradaConsola.texto("Placa"),
                EntradaConsola.texto("Id del conductor"));
        System.out.println("Conductores de " + vehiculo.getPlaca() + ": " + vehiculo.getIdsConductores());
    }

    public void retirarConductor(Sesion sesion) {
        Vehiculo vehiculo = servicio.retirarConductor(sesion, EntradaConsola.texto("Placa"),
                EntradaConsola.texto("Id del conductor"));
        System.out.println("Conductores de " + vehiculo.getPlaca() + ": " + vehiculo.getIdsConductores());
    }
}
