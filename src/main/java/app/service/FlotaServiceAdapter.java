package app.service;

import app.domain.Empleado;
import app.domain.Sesion;
import app.domain.Vehiculo;
import app.domain.enums.CargoEmpleado;
import app.domain.enums.Rol;
import app.domain.exceptions.RecursoNoEncontradoException;
import app.domain.exceptions.ReglaNegocioException;
import app.service.helpers.ControlAcceso;
import app.service.inputports.FlotaServiceInterface;
import app.service.inputports.dto.RegistroVehiculo;
import app.service.outputports.EmpleadoRepositoryPort;
import app.service.outputports.VehiculoRepositoryPort;
import app.service.validators.DatosValidator;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class FlotaServiceAdapter implements FlotaServiceInterface {

    private final VehiculoRepositoryPort vehiculos;
    private final EmpleadoRepositoryPort empleados;
    private final Clock reloj;

    public FlotaServiceAdapter(VehiculoRepositoryPort vehiculos, EmpleadoRepositoryPort empleados, Clock reloj) {
        this.vehiculos = vehiculos;
        this.empleados = empleados;
        this.reloj = reloj;
    }

    @Override
    public Vehiculo registrarVehiculo(Sesion sesion, RegistroVehiculo datos) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        String placa = DatosValidator.placa(datos.placa());
        if (vehiculos.buscarPorPlaca(placa).isPresent()) {
            throw new ReglaNegocioException("Ya existe un vehículo con placa " + placa);
        }
        Vehiculo vehiculo = new Vehiculo(placa, DatosValidator.requerido(datos.tipo(), "tipo de vehículo"),
                DatosValidator.requerido(datos.modelo(), "modelo"),
                DatosValidator.positivo(datos.capacidadCargaKg(), "capacidad de carga"),
                DatosValidator.positivo(datos.capacidadVolumenM3(), "capacidad en m³"),
                DatosValidator.requerido(datos.vencimientoSoat(), "vencimiento del SOAT"),
                DatosValidator.requerido(datos.vencimientoTecnomecanica(), "vencimiento de la tecnomecánica"));
        vehiculo.sincronizarEstadoConDocumentos(LocalDate.now(reloj));
        return vehiculos.guardar(vehiculo);
    }

    @Override
    public List<Vehiculo> listarVehiculos(Sesion sesion) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        LocalDate hoy = LocalDate.now(reloj);
        List<Vehiculo> flota = vehiculos.listar();
        for (Vehiculo vehiculo : flota) {
            var estadoAnterior = vehiculo.getEstado();
            vehiculo.sincronizarEstadoConDocumentos(hoy);
            if (vehiculo.getEstado() != estadoAnterior) {
                vehiculos.guardar(vehiculo);
            }
        }
        return flota;
    }

    @Override
    public Vehiculo asignarConductor(Sesion sesion, String placa, String idEmpleado) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        Vehiculo vehiculo = buscarVehiculo(placa);
        Empleado empleado = empleados.buscarPorId(idEmpleado)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el empleado " + idEmpleado));
        if (empleado.getCargo() != CargoEmpleado.CONDUCTOR) {
            throw new ReglaNegocioException(empleado.getNombre() + " no tiene el cargo de conductor");
        }
        if (!empleado.esAsignable()) {
            throw new ReglaNegocioException(empleado.getNombre() + " no está activo");
        }
        vehiculo.asignarConductor(idEmpleado);
        return vehiculos.guardar(vehiculo);
    }

    @Override
    public Vehiculo retirarConductor(Sesion sesion, String placa, String idEmpleado) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        Vehiculo vehiculo = buscarVehiculo(placa);
        vehiculo.retirarConductor(idEmpleado);
        return vehiculos.guardar(vehiculo);
    }

    private Vehiculo buscarVehiculo(String placa) {
        String normalizada = DatosValidator.placa(placa);
        return vehiculos.buscarPorPlaca(normalizada)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el vehículo " + normalizada));
    }
}
