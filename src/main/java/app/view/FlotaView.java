package app.view;

import app.domain.Sesion;
import app.domain.Vehiculo;
import app.domain.enums.TipoVehiculo;
import app.service.inputports.FlotaServiceInterface;
import app.service.inputports.dto.RegistroVehiculo;
import app.view.helpers.EntradaConsola;
import app.view.helpers.Formato;
import app.view.helpers.Pantalla;
import app.view.helpers.Tabla;

import java.util.List;

public class FlotaView {

    private final FlotaServiceInterface servicio;

    public FlotaView(FlotaServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void registrar(Sesion sesion) {
        Pantalla.subtitulo("Registro de vehículo");
        RegistroVehiculo datos = new RegistroVehiculo(EntradaConsola.texto("Placa"),
                EntradaConsola.opcion("Tipo de vehículo", TipoVehiculo.values(), TipoVehiculo::getEtiqueta),
                EntradaConsola.texto("Modelo"), EntradaConsola.decimal("Capacidad de carga (kg)"),
                EntradaConsola.decimal("Capacidad de volumen (m³)"), EntradaConsola.fecha("Vencimiento del SOAT"),
                EntradaConsola.fecha("Vencimiento de la tecnomecánica"));
        Vehiculo vehiculo = servicio.registrarVehiculo(sesion, datos);
        Pantalla.exito("Vehículo " + vehiculo.getPlaca() + " registrado. Estado: " + vehiculo.getEstado().getEtiqueta());
    }

    public void listar(Sesion sesion) {
        List<Vehiculo> flota = servicio.listarVehiculos(sesion);
        Pantalla.subtitulo("Flota");
        Tabla tabla = new Tabla("Placa", "Tipo", "Modelo", "Carga", "Volumen", "SOAT", "Tecnomecánica", "Estado",
                "Conductores");
        for (Vehiculo v : flota) {
            tabla.fila(v.getPlaca(), v.getTipo().getEtiqueta(), v.getModelo(),
                    Formato.decimal(v.getCapacidadCargaKg()) + " kg", Formato.decimal(v.getCapacidadVolumenM3()) + " m³",
                    v.getVencimientoSoat(), v.getVencimientoTecnomecanica(), v.getEstado().getEtiqueta(),
                    Formato.lista(v.getIdsConductores()));
        }
        tabla.imprimir("No hay vehículos registrados.");
    }

    public void asignarConductor(Sesion sesion) {
        Pantalla.subtitulo("Asignar conductor");
        Vehiculo vehiculo = servicio.asignarConductor(sesion, EntradaConsola.texto("Placa"),
                EntradaConsola.texto("Id del conductor"));
        Pantalla.exito("Conductores de " + vehiculo.getPlaca() + ": " + Formato.lista(vehiculo.getIdsConductores()));
    }

    public void retirarConductor(Sesion sesion) {
        Pantalla.subtitulo("Retirar conductor");
        Vehiculo vehiculo = servicio.retirarConductor(sesion, EntradaConsola.texto("Placa"),
                EntradaConsola.texto("Id del conductor"));
        Pantalla.exito("Conductores de " + vehiculo.getPlaca() + ": " + Formato.lista(vehiculo.getIdsConductores()));
    }
}
