package app.service.inputports;

import app.domain.Sesion;
import app.domain.Vehiculo;
import app.service.inputports.dto.RegistroVehiculo;

import java.util.List;

public interface FlotaServiceInterface {

    Vehiculo registrarVehiculo(Sesion sesion, RegistroVehiculo datos);

    List<Vehiculo> listarVehiculos(Sesion sesion);

    Vehiculo asignarConductor(Sesion sesion, String placa, String idEmpleado);

    Vehiculo retirarConductor(Sesion sesion, String placa, String idEmpleado);
}
