package app.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class EmpleadoTest {

    @Test
    void constructorSoloConIdEmpleado_asignaElId() {
        Empleado empleado = new Empleado("EMP-001");

        assertEquals("EMP-001", empleado.getIdEmpleado());
        assertNull(empleado.getNombre());
    }

    @Test
    void constructorConArgumentos_asignaCamposPropiosYHerenciaDePersona() {
        Empleado empleado = new Empleado("Luis Gómez", "555666777", "3215554444", "luis@example.com", "EMP-002");

        assertEquals("Luis Gómez", empleado.getNombre());
        assertEquals("555666777", empleado.getCedula());
        assertEquals("3215554444", empleado.getTelefono());
        assertEquals("luis@example.com", empleado.getEmail());
        assertEquals("EMP-002", empleado.getIdEmpleado());
    }

    @Test
    void setters_deCargoYTurno_actualizanLosValores() {
        Empleado empleado = new Empleado("EMP-003");

        empleado.setCargo("Conductor");
        empleado.setTurno("Mañana");

        assertEquals("Conductor", empleado.getCargo());
        assertEquals("Mañana", empleado.getTurno());
    }

    @Test
    void setterDeIdEmpleado_actualizaElValor() {
        Empleado empleado = new Empleado("EMP-004");
        empleado.setIdEmpleado("EMP-005");

        assertEquals("EMP-005", empleado.getIdEmpleado());
    }

    @Test
    void settersHeredadosDePersona_actualizanLosValores() {
        Empleado empleado = new Empleado("EMP-006");

        empleado.setNombre("Laura Díaz");
        empleado.setCedula("222333444");
        empleado.setTelefono("3222221111");
        empleado.setEmail("laura@example.com");

        assertEquals("Laura Díaz", empleado.getNombre());
        assertEquals("222333444", empleado.getCedula());
        assertEquals("3222221111", empleado.getTelefono());
        assertEquals("laura@example.com", empleado.getEmail());
    }
}