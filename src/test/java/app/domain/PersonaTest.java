package app.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class PersonaTest {

    @Test
    void constructorConArgumentos_asignaTodosLosCampos() {
        Persona persona = new Persona("Juan Pérez", "123456789", "3001234567", "juan@example.com");

        assertEquals("Juan Pérez", persona.getNombre());
        assertEquals("123456789", persona.getCedula());
        assertEquals("3001234567", persona.getTelefono());
        assertEquals("juan@example.com", persona.getEmail());
    }

    @Test
    void constructorPorDefecto_dejaLosCamposEnNull() {
        Persona persona = new Persona();

        assertNull(persona.getNombre());
        assertNull(persona.getCedula());
        assertNull(persona.getTelefono());
        assertNull(persona.getEmail());
    }

    @Test
    void setters_actualizanLosValoresDeLosCampos() {
        Persona persona = new Persona();

        persona.setNombre("María García");
        persona.setCedula("987654321");
        persona.setTelefono("3019876543");
        persona.setEmail("maria@example.com");

        assertEquals("María García", persona.getNombre());
        assertEquals("987654321", persona.getCedula());
        assertEquals("3019876543", persona.getTelefono());
        assertEquals("maria@example.com", persona.getEmail());
    }
}