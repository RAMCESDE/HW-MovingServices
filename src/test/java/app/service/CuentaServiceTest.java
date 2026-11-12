package app.service;

import app.domain.Sesion;
import app.domain.enums.Rol;
import app.domain.exceptions.AccesoDenegadoException;
import app.domain.exceptions.DatoInvalidoException;
import app.domain.exceptions.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Épica 1: US-01, US-02, US-03 y US-04. */
class CuentaServiceTest {

    private final Entorno e = new Entorno();

    @Test
    void us01_registroExitosoCreaCuentaHasheadaYEnviaVerificacion() {
        var cuenta = e.s.cuenta().registrarCliente("Ana@Correo.com", Entorno.CLAVE, true);

        assertEquals("ana@correo.com", cuenta.getEmail());
        assertNotEquals(Entorno.CLAVE, cuenta.getPasswordHash());
        assertEquals("ana@correo.com", e.correos.get(0).destinatario());

        e.s.cuenta().verificarCorreo(e.ultimoToken());
        assertTrue(e.repos.cuentas().buscarPorEmail("ana@correo.com").orElseThrow().isCorreoVerificado());
    }

    @Test
    void us01_rechazaCorreoDuplicado() {
        e.s.cuenta().registrarCliente("ana@correo.com", Entorno.CLAVE, true);

        var error = assertThrows(ReglaNegocioException.class,
                () -> e.s.cuenta().registrarCliente("ANA@correo.com", Entorno.CLAVE, true));
        assertTrue(error.getMessage().contains("iniciar sesión"));
    }

    @Test
    void us01_validaContrasenaCorreoYTerminos() {
        for (String clave : new String[]{"Corta1*", "sinmayuscula1*", "SinNumero*", "SinEspecial1"}) {
            assertThrows(DatoInvalidoException.class, () -> e.s.cuenta().registrarCliente("a@b.co", clave, true), clave);
        }
        assertThrows(DatoInvalidoException.class, () -> e.s.cuenta().registrarCliente("no-es-correo", Entorno.CLAVE, true));
        assertThrows(DatoInvalidoException.class, () -> e.s.cuenta().registrarCliente("a@b.co", Entorno.CLAVE, false));
    }

    @Test
    void us02_recuperacionCambiaLaContrasenaYElTokenEsDeUnSoloUso() {
        e.s.cuenta().registrarCliente("ana@correo.com", Entorno.CLAVE, true);
        e.s.cuenta().solicitarRecuperacion("ana@correo.com");
        String token = e.ultimoToken();

        e.s.cuenta().restablecerContrasena(token, "NuevaClave9!");

        assertEquals(Rol.CLIENTE, e.s.cuenta().iniciarSesion("ana@correo.com", "NuevaClave9!").rol());
        assertThrows(AccesoDenegadoException.class, () -> e.s.cuenta().iniciarSesion("ana@correo.com", Entorno.CLAVE));
        assertThrows(DatoInvalidoException.class, () -> e.s.cuenta().restablecerContrasena(token, "OtraClave9!"));
    }

    @Test
    void us02_elEnlaceExpiraALosQuinceMinutos() {
        e.s.cuenta().registrarCliente("ana@correo.com", Entorno.CLAVE, true);
        e.s.cuenta().solicitarRecuperacion("ana@correo.com");
        String token = e.ultimoToken();

        e.reloj.avanzar(Duration.ofMinutes(15));

        var error = assertThrows(ReglaNegocioException.class, () -> e.s.cuenta().restablecerContrasena(token, "NuevaClave9!"));
        assertTrue(error.getMessage().contains("expirado"));
    }

    @Test
    void us02_unNuevoEnlaceInvalidaElAnterior() {
        e.s.cuenta().registrarCliente("ana@correo.com", Entorno.CLAVE, true);
        e.s.cuenta().solicitarRecuperacion("ana@correo.com");
        String anterior = e.ultimoToken();
        e.s.cuenta().solicitarRecuperacion("ana@correo.com");

        assertThrows(DatoInvalidoException.class, () -> e.s.cuenta().restablecerContrasena(anterior, "NuevaClave9!"));
    }

    @Test
    void us02_unCorreoNoRegistradoNoRevelaNadaNiEnviaCorreo() {
        e.s.cuenta().solicitarRecuperacion("nadie@correo.com");

        assertTrue(e.correos.isEmpty());
    }

    @Test
    void us03_loginEntregaRolYRechazaCredencialesInvalidas() {
        e.s.cuenta().registrarCliente("ana@correo.com", Entorno.CLAVE, true);

        assertEquals(Rol.CLIENTE, e.s.cuenta().iniciarSesion(" ANA@correo.com ", Entorno.CLAVE).rol());
        var malaClave = assertThrows(AccesoDenegadoException.class, () -> e.s.cuenta().iniciarSesion("ana@correo.com", "x"));
        var noExiste = assertThrows(AccesoDenegadoException.class, () -> e.s.cuenta().iniciarSesion("no@correo.com", "x"));
        assertEquals(malaClave.getMessage(), noExiste.getMessage());
    }

    @Test
    void us03_unClienteNoPuedeEjecutarOperacionesDeAdministrador() {
        Sesion cliente = e.cliente();

        assertThrows(AccesoDenegadoException.class, () -> e.s.flota().listarVehiculos(cliente));
        assertThrows(AccesoDenegadoException.class, () -> e.s.empleado().listarEmpleados(cliente));
        assertThrows(AccesoDenegadoException.class, () -> e.s.programacion().listarPorProgramar(cliente));
    }

    @Test
    void us04_editaPerfilYValidaTelefono() {
        Sesion cliente = e.cliente();

        e.s.cliente().actualizarPerfil(cliente, "Ana Torres", "300 123 4567", "Calle 1");
        e.s.cliente().actualizarPerfil(cliente, null, null, "Calle 2");

        var perfil = e.s.cliente().obtenerPerfil(cliente);
        assertEquals("Ana Torres", perfil.getNombre());
        assertEquals("3001234567", perfil.getTelefono());
        assertEquals("Calle 2", perfil.getDireccion());
        assertThrows(DatoInvalidoException.class, () -> e.s.cliente().actualizarPerfil(cliente, null, "123", null));
    }
}
