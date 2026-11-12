package app.view;

import app.domain.Cuenta;
import app.domain.Sesion;
import app.service.inputports.CuentaServiceInterface;
import app.view.helpers.EntradaConsola;
import app.view.helpers.Pantalla;

public class CuentaView {

    private final CuentaServiceInterface servicio;

    public CuentaView(CuentaServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void registrarCliente() {
        Pantalla.subtitulo("Registro de cliente");
        String email = EntradaConsola.texto("Correo electrónico");
        Pantalla.info("La contraseña debe tener mínimo 8 caracteres, una mayúscula, un número y un carácter especial.");
        String password = EntradaConsola.texto("Contraseña");
        boolean terminos = EntradaConsola.siNo("¿Acepta los términos y condiciones?");
        Cuenta cuenta = servicio.registrarCliente(email, password, terminos);
        Pantalla.exito("Cuenta creada para " + cuenta.getEmail() + ". Revise su correo para verificarla.");
    }

    public void verificarCorreo() {
        Pantalla.subtitulo("Verificar correo");
        servicio.verificarCorreo(EntradaConsola.texto("Código de verificación"));
        Pantalla.exito("Correo verificado.");
    }

    public void solicitarRecuperacion() {
        Pantalla.subtitulo("Recuperar contraseña");
        servicio.solicitarRecuperacion(EntradaConsola.texto("Correo electrónico"));
        Pantalla.info("Si el correo está registrado, recibirá un enlace vigente por 15 minutos.");
    }

    public void restablecerContrasena() {
        Pantalla.subtitulo("Restablecer contraseña");
        String token = EntradaConsola.texto("Token del enlace");
        String nueva = EntradaConsola.texto("Nueva contraseña");
        servicio.restablecerContrasena(token, nueva);
        Pantalla.exito("Contraseña actualizada. Ya puede iniciar sesión.");
    }

    public Sesion iniciarSesion() {
        Pantalla.subtitulo("Iniciar sesión");
        String email = EntradaConsola.texto("Correo electrónico");
        String password = EntradaConsola.texto("Contraseña");
        Sesion sesion = servicio.iniciarSesion(email, password);
        Pantalla.exito("Bienvenido, " + sesion.email() + " (" + sesion.rol().getEtiqueta() + ")");
        return sesion;
    }
}
