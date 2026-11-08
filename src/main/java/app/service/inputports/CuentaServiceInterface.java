package app.service.inputports;

import app.domain.Cuenta;
import app.domain.Sesion;

public interface CuentaServiceInterface {

    Cuenta registrarCliente(String email, String password, boolean aceptaTerminos);

    void verificarCorreo(String token);

    void solicitarRecuperacion(String email);

    void restablecerContrasena(String token, String nuevaContrasena);

    Sesion iniciarSesion(String email, String password);
}
