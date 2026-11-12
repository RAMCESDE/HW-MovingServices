package app.view;

import app.domain.Empleado;
import app.domain.Sesion;
import app.domain.enums.CargoEmpleado;
import app.domain.enums.EstadoEmpleado;
import app.service.inputports.EmpleadoServiceInterface;
import app.service.inputports.dto.RegistroEmpleado;
import app.view.helpers.EntradaConsola;
import app.view.helpers.Formato;
import app.view.helpers.Pantalla;
import app.view.helpers.Tabla;

import java.util.List;

public class EmpleadoView {

    private final EmpleadoServiceInterface servicio;

    public EmpleadoView(EmpleadoServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void registrar(Sesion sesion) {
        Pantalla.subtitulo("Registro de empleado");
        String nombre = EntradaConsola.texto("Nombre");
        String cedula = EntradaConsola.texto("Documento");
        String telefono = EntradaConsola.texto("Teléfono");
        String email = EntradaConsola.texto("Correo");
        String password = EntradaConsola.texto("Contraseña temporal");
        CargoEmpleado cargo = EntradaConsola.opcion("Cargo", CargoEmpleado.values(), CargoEmpleado::getEtiqueta);
        String turno = EntradaConsola.textoOpcional("Turno");
        int edad = EntradaConsola.entero("Edad");
        String sexo = EntradaConsola.textoOpcional("Sexo");
        String estadoCivil = EntradaConsola.textoOpcional("Estado civil");
        String eps = EntradaConsola.textoOpcional("EPS");
        String licencia = EntradaConsola.textoOpcional("Tipo de licencia (vacío si no aplica)");
        var vencimiento = EntradaConsola.fechaOpcional("Vencimiento de la licencia");
        String talla = EntradaConsola.textoOpcional("Talla de dotación");
        Empleado empleado = servicio.registrarEmpleado(sesion, new RegistroEmpleado(nombre, cedula, telefono, email,
                password, cargo, turno, edad, sexo, estadoCivil, eps, licencia, vencimiento, talla));
        Pantalla.exito("Empleado registrado con id " + empleado.getIdEmpleado());
    }

    public void listar(Sesion sesion) {
        List<Empleado> empleados = servicio.listarEmpleados(sesion);
        Pantalla.subtitulo("Empleados");
        Tabla tabla = new Tabla("Id", "Nombre", "Cargo", "Teléfono", "Estado", "Calificación");
        for (Empleado e : empleados) {
            tabla.fila(e.getIdEmpleado(), e.getNombre(), e.getCargo().getEtiqueta(), e.getTelefono(),
                    e.getEstado().getEtiqueta(),
                    Formato.decimal(e.getCalificacionPromedio()) + " (" + e.getTotalCalificaciones() + ")");
        }
        tabla.imprimir("No hay empleados registrados.");
    }

    public void cambiarEstado(Sesion sesion) {
        String id = EntradaConsola.texto("Id del empleado");
        EstadoEmpleado estado = EntradaConsola.opcion("Nuevo estado", EstadoEmpleado.values(), EstadoEmpleado::getEtiqueta);
        Empleado empleado = servicio.cambiarEstado(sesion, id, estado);
        Pantalla.exito(empleado.getNombre() + " ahora está " + empleado.getEstado().getEtiqueta());
    }
}
