package app.view;

import app.domain.Cuenta;
import app.domain.Sesion;
import app.service.inputports.CuentaServiceInterface;
import app.view.helpers.EntradaConsola;

public class CuentaView {

    private final CuentaServiceInterface servicio;

    public CuentaView(CuentaServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void registrarCliente() {
        String email = EntradaConsola.texto("Correo electrónico");
        System.out.println("La contraseña debe tener mínimo 8 caracteres, una mayúscula, un número y un carácter especial");
        String password = EntradaConsola.texto("Contraseña");
        boolean terminos = EntradaConsola.siNo("¿Acepta los términos y condiciones?");
        Cuenta cuenta = servicio.registrarCliente(email, password, terminos);
        System.out.println("Cuenta creada para " + cuenta.getEmail() + ". Revise su correo para verificarla.");
    }

    public void verificarCorreo() {
        servicio.verificarCorreo(EntradaConsola.texto("Código de verificación"));
        System.out.println("Correo verificado.");
    }

    public void solicitarRecuperacion() {
        servicio.solicitarRecuperacion(EntradaConsola.texto("Correo electrónico"));
        System.out.println("Si el correo está registrado, recibirá un enlace vigente por 15 minutos.");
    }

    public void restablecerContrasena() {
        String token = EntradaConsola.texto("Token del enlace");
        String nueva = EntradaConsola.texto("Nueva contraseña");
        servicio.restablecerContrasena(token, nueva);
        System.out.println("Contraseña actualizada. Ya puede iniciar sesión.");
    }

    public Sesion iniciarSesion() {
        String email = EntradaConsola.texto("Correo electrónico");
        String password = EntradaConsola.texto("Contraseña");
        Sesion sesion = servicio.iniciarSesion(email, password);
        System.out.println("Bienvenido, " + sesion.email() + " (" + sesion.rol().getEtiqueta() + ")");
        return sesion;
    }
}
