package app.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class ClienteTest {

    @Test
    void constructorConArgumentos_asignaCamposPropiosYHerenciaDePersona() {
        Cliente cliente = new Cliente("Ana Torres", "111222333", "3101112222", "ana@example.com", "Calle 10 #20-30");

        assertEquals("Ana Torres", cliente.getNombre());
        assertEquals("111222333", cliente.getCedula());
        assertEquals("3101112222", cliente.getTelefono());
        assertEquals("ana@example.com", cliente.getEmail());
        assertEquals("Calle 10 #20-30", cliente.getDireccion());
    }

    @Test
    void constructorPorDefecto_dejaLosCamposEnNull() {
        Cliente cliente = new Cliente();

        assertNull(cliente.getNombre());
        assertNull(cliente.getDireccion());
    }

    @Test
    void setterDeDireccion_actualizaElValor() {
        Cliente cliente = new Cliente();
        cliente.setDireccion("Av. Siempre Viva 742");

        assertEquals("Av. Siempre Viva 742", cliente.getDireccion());
    }

    @Test
    void setterDeNombreHeredado_actualizaElValor() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Camilo Ruiz");

        assertEquals("Camilo Ruiz", cliente.getNombre());
    }

    @Test
    void registrarServicio_noLanzaExcepciones() {
        Cliente cliente = new Cliente();

        assertDoesNotThrow(cliente::registrarServicio);
    }
}