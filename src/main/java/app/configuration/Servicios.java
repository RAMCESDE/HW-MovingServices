package app.configuration;

import app.service.ClienteServiceAdapter;
import app.service.CuentaServiceAdapter;
import app.service.inputports.ClienteServiceInterface;
import app.service.inputports.CuentaServiceInterface;
import app.service.outputports.NotificacionPort;
import app.service.outputports.PasswordHasherPort;

import java.time.Clock;

/** Conecta cada caso de uso con los adaptadores que recibe; la UI solo conoce las interfaces. */
public record Servicios(CuentaServiceInterface cuenta,
                        ClienteServiceInterface cliente) {

    public static Servicios crear(Repositorios r, PasswordHasherPort hasher, NotificacionPort notificacion,
                                  Clock reloj) {
        return new Servicios(
                new CuentaServiceAdapter(r.cuentas(), r.clientes(), r.tokens(), hasher, notificacion, r.transaccion(), reloj),
                new ClienteServiceAdapter(r.clientes()));
    }
}
