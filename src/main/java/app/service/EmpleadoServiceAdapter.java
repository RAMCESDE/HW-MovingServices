package app.service;

import app.domain.Cuenta;
import app.domain.Empleado;
import app.domain.Sesion;
import app.domain.enums.CargoEmpleado;
import app.domain.enums.EstadoEmpleado;
import app.domain.enums.Rol;
import app.domain.exceptions.DatoInvalidoException;
import app.domain.exceptions.RecursoNoEncontradoException;
import app.domain.exceptions.ReglaNegocioException;
import app.service.helpers.ControlAcceso;
import app.service.helpers.IdGenerator;
import app.service.inputports.EmpleadoServiceInterface;
import app.service.inputports.dto.RegistroEmpleado;
import app.service.outputports.CuentaRepositoryPort;
import app.service.outputports.EmpleadoRepositoryPort;
import app.service.outputports.PasswordHasherPort;
import app.service.outputports.TransaccionPort;
import app.service.validators.DatosValidator;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class EmpleadoServiceAdapter implements EmpleadoServiceInterface {

    private final EmpleadoRepositoryPort empleados;
    private final CuentaRepositoryPort cuentas;
    private final PasswordHasherPort hasher;
    private final TransaccionPort transaccion;
    private final Clock reloj;

    public EmpleadoServiceAdapter(EmpleadoRepositoryPort empleados, CuentaRepositoryPort cuentas,
                                  PasswordHasherPort hasher, TransaccionPort transaccion, Clock reloj) {
        this.empleados = empleados;
        this.cuentas = cuentas;
        this.hasher = hasher;
        this.transaccion = transaccion;
        this.reloj = reloj;
    }

    @Override
    public Empleado registrarEmpleado(Sesion sesion, RegistroEmpleado datos) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        String correo = DatosValidator.email(datos.email());
        String cedula = DatosValidator.requerido(datos.cedula(), "documento");
        DatosValidator.password(datos.passwordTemporal());
        if (datos.cargo() == null) {
            throw new DatoInvalidoException("El campo cargo es obligatorio");
        }
        if (datos.cargo() == CargoEmpleado.CONDUCTOR
                && (datos.tipoLicencia() == null || datos.tipoLicencia().isBlank() || datos.vencimientoLicencia() == null)) {
            throw new DatoInvalidoException("El conductor debe tener tipo y vencimiento de licencia");
        }
        if (cuentas.buscarPorEmail(correo).isPresent()) {
            throw new ReglaNegocioException("El correo " + correo + " ya está registrado");
        }
        if (empleados.buscarPorCedula(cedula).isPresent()) {
            throw new ReglaNegocioException("Ya existe un empleado con documento " + cedula);
        }
        Empleado empleado = new Empleado(DatosValidator.requerido(datos.nombre(), "nombre"), cedula,
                DatosValidator.telefono(datos.telefono()), correo, IdGenerator.nuevo("EMP"));
        empleado.setCargo(datos.cargo());
        empleado.setTurno(datos.turno());
        empleado.setEdad(datos.edad());
        empleado.setSexo(datos.sexo());
        empleado.setEstadoCivil(datos.estadoCivil());
        empleado.setEps(datos.eps());
        empleado.setTipoLicencia(datos.tipoLicencia());
        empleado.setVencimientoLicencia(datos.vencimientoLicencia());
        empleado.setTallaDotacion(datos.tallaDotacion());

        Rol rol = datos.cargo() == CargoEmpleado.CONDUCTOR ? Rol.CONDUCTOR : Rol.AUXILIAR_CARGA;
        Cuenta cuenta = new Cuenta(IdGenerator.nuevo("CTA"), correo, hasher.hashear(datos.passwordTemporal()), rol,
                empleado.getIdEmpleado(), LocalDateTime.now(reloj));
        return transaccion.ejecutar(() -> {
            empleados.guardar(empleado);
            cuentas.guardar(cuenta);
            return empleado;
        });
    }

    @Override
    public List<Empleado> listarEmpleados(Sesion sesion) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        return empleados.listar();
    }

    @Override
    public Empleado cambiarEstado(Sesion sesion, String idEmpleado, EstadoEmpleado nuevoEstado) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        Empleado empleado = empleados.buscarPorId(idEmpleado)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el empleado " + idEmpleado));
        empleado.cambiarEstado(DatosValidator.requerido(nuevoEstado, "estado"));
        return transaccion.ejecutar(() -> {
            empleados.guardar(empleado);
            cuentas.buscarPorPerfil(idEmpleado).ifPresent(cuenta -> {
                cuenta.setActiva(nuevoEstado != EstadoEmpleado.INACTIVO);
                cuentas.guardar(cuenta);
            });
            return empleado;
        });
    }
}
