package app.service;

import app.domain.Cliente;
import app.domain.Cuenta;
import app.domain.Sesion;
import app.domain.TokenCuenta;
import app.domain.enums.Rol;
import app.domain.enums.TipoTokenCuenta;
import app.domain.exceptions.AccesoDenegadoException;
import app.domain.exceptions.DatoInvalidoException;
import app.domain.exceptions.ReglaNegocioException;
import app.service.helpers.IdGenerator;
import app.service.helpers.TokenHelper;
import app.service.inputports.CuentaServiceInterface;
import app.service.outputports.ClienteRepositoryPort;
import app.service.outputports.CuentaRepositoryPort;
import app.service.outputports.NotificacionPort;
import app.service.outputports.PasswordHasherPort;
import app.service.outputports.TokenCuentaRepositoryPort;
import app.service.outputports.TransaccionPort;
import app.service.validators.DatosValidator;

import java.time.Clock;
import java.time.LocalDateTime;

public class CuentaServiceAdapter implements CuentaServiceInterface {

    private static final int MINUTOS_RECUPERACION = 15;
    private static final int HORAS_VERIFICACION = 24;

    private final CuentaRepositoryPort cuentas;
    private final ClienteRepositoryPort clientes;
    private final TokenCuentaRepositoryPort tokens;
    private final PasswordHasherPort hasher;
    private final NotificacionPort notificacion;
    private final TransaccionPort transaccion;
    private final Clock reloj;

    public CuentaServiceAdapter(CuentaRepositoryPort cuentas, ClienteRepositoryPort clientes,
                                TokenCuentaRepositoryPort tokens, PasswordHasherPort hasher,
                                NotificacionPort notificacion, TransaccionPort transaccion, Clock reloj) {
        this.cuentas = cuentas;
        this.clientes = clientes;
        this.tokens = tokens;
        this.hasher = hasher;
        this.notificacion = notificacion;
        this.transaccion = transaccion;
        this.reloj = reloj;
    }

    @Override
    public Cuenta registrarCliente(String email, String password, boolean aceptaTerminos) {
        String correo = DatosValidator.email(email);
        DatosValidator.password(password);
        if (!aceptaTerminos) {
            throw new DatoInvalidoException("Debe aceptar los términos y condiciones");
        }
        if (cuentas.buscarPorEmail(correo).isPresent()) {
            throw new ReglaNegocioException("El correo " + correo + " ya está registrado. Puede iniciar sesión");
        }
        LocalDateTime ahora = LocalDateTime.now(reloj);
        String idCliente = IdGenerator.nuevo("CLI");
        Cuenta cuenta = new Cuenta(IdGenerator.nuevo("CTA"), correo, hasher.hashear(password), Rol.CLIENTE, idCliente, ahora);
        String token = TokenHelper.generar();

        transaccion.ejecutar(() -> {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(idCliente);
            cliente.setEmail(correo);
            clientes.guardar(cliente);
            cuentas.guardar(cuenta);
            tokens.guardar(new TokenCuenta(TokenHelper.hash(token), cuenta.getIdCuenta(),
                    TipoTokenCuenta.VERIFICACION_CORREO, ahora.plusHours(HORAS_VERIFICACION)));
            return cuenta;
        });
        notificacion.enviarCorreo(correo, "Verifique su correo en HW Moving Services",
                "Su código de verificación es: " + token + " (vigente por " + HORAS_VERIFICACION + " horas)");
        return cuenta;
    }

    @Override
    public void verificarCorreo(String token) {
        TokenCuenta registro = tokenValido(token, TipoTokenCuenta.VERIFICACION_CORREO);
        Cuenta cuenta = cuentaDe(registro);
        cuenta.setCorreoVerificado(true);
        registro.setUsado(true);
        transaccion.ejecutar(() -> {
            cuentas.guardar(cuenta);
            return tokens.guardar(registro);
        });
    }

    @Override
    public void solicitarRecuperacion(String email) {
        // No se revela si el correo existe: la respuesta al usuario es la misma en ambos casos.
        cuentas.buscarPorEmail(DatosValidator.email(email)).filter(Cuenta::isActiva).ifPresent(cuenta -> {
            String token = TokenHelper.generar();
            transaccion.ejecutar(() -> {
                tokens.invalidarPendientes(cuenta.getIdCuenta(), TipoTokenCuenta.RECUPERACION_CONTRASENA);
                return tokens.guardar(new TokenCuenta(TokenHelper.hash(token), cuenta.getIdCuenta(),
                        TipoTokenCuenta.RECUPERACION_CONTRASENA,
                        LocalDateTime.now(reloj).plusMinutes(MINUTOS_RECUPERACION)));
            });
            notificacion.enviarCorreo(cuenta.getEmail(), "Recuperación de contraseña",
                    "Use este enlace en los próximos " + MINUTOS_RECUPERACION
                            + " minutos: https://hwmoving.example/recuperar?token=" + token);
        });
    }

    @Override
    public void restablecerContrasena(String token, String nuevaContrasena) {
        DatosValidator.password(nuevaContrasena);
        TokenCuenta registro = tokenValido(token, TipoTokenCuenta.RECUPERACION_CONTRASENA);
        Cuenta cuenta = cuentaDe(registro);
        cuenta.setPasswordHash(hasher.hashear(nuevaContrasena));
        registro.setUsado(true);
        transaccion.ejecutar(() -> {
            cuentas.guardar(cuenta);
            return tokens.guardar(registro);
        });
    }

    @Override
    public Sesion iniciarSesion(String email, String password) {
        String correo = email == null ? "" : email.trim().toLowerCase(java.util.Locale.ROOT);
        Cuenta cuenta = cuentas.buscarPorEmail(correo)
                .filter(c -> password != null && hasher.coincide(password, c.getPasswordHash()))
                .orElseThrow(() -> new AccesoDenegadoException("Correo o contraseña incorrectos"));
        if (!cuenta.isActiva()) {
            throw new AccesoDenegadoException("Su cuenta está inactiva. Comuníquese con el administrador");
        }
        return new Sesion(cuenta.getIdCuenta(), cuenta.getIdPerfil(), cuenta.getEmail(), cuenta.getRol());
    }

    private TokenCuenta tokenValido(String token, TipoTokenCuenta tipo) {
        TokenCuenta registro = tokens.buscarPorHash(TokenHelper.hash(DatosValidator.requerido(token, "token")))
                .filter(t -> t.getTipo() == tipo && !t.isUsado())
                .orElseThrow(() -> new DatoInvalidoException("El enlace o código no es válido"));
        if (registro.expirado(LocalDateTime.now(reloj))) {
            throw new ReglaNegocioException("El enlace ha expirado. Genere una nueva solicitud");
        }
        return registro;
    }

    private Cuenta cuentaDe(TokenCuenta registro) {
        return cuentas.buscarPorId(registro.getIdCuenta())
                .orElseThrow(() -> new DatoInvalidoException("El enlace o código no es válido"));
    }
}
