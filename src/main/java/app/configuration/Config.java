package app.configuration;

import app.domain.Cuenta;
import app.domain.enums.Rol;
import app.persistence.dbconnection.DataBaseConnection;
import app.persistence.external.NotificacionConsolaAdapter;
import app.persistence.external.Pbkdf2PasswordHasherAdapter;
import app.service.helpers.IdGenerator;
import app.service.outputports.PasswordHasherPort;
import app.ui.CliUserInterface;
import app.view.ClienteView;
import app.view.CuentaView;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

/** Raíz de composición: la única clase que conoce las implementaciones concretas. */
public class Config {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    public static CliUserInterface getCliUserInterface() {
        AppProperties propiedades = new AppProperties();
        Clock reloj = Clock.system(ZONA);
        PasswordHasherPort hasher = new Pbkdf2PasswordHasherAdapter();

        Repositorios repositorios = crearRepositorios(propiedades);
        crearAdministradorInicial(repositorios, hasher, propiedades, reloj);

        Servicios s = Servicios.crear(repositorios, hasher, new NotificacionConsolaAdapter(), reloj);

        return new CliUserInterface(new CuentaView(s.cuenta()),
                new ClienteView(s.cliente()));
    }

    private static Repositorios crearRepositorios(AppProperties propiedades) {
        if ("mysql".equalsIgnoreCase(propiedades.get("persistence.mode"))) {
            return Repositorios.enBaseDeDatos(DataBaseConnection.getInstance(propiedades.get("db.url"),
                    propiedades.get("db.user"), propiedades.get("db.password")).getConnection());
        }
        return Repositorios.enMemoria();
    }

    private static void crearAdministradorInicial(Repositorios repositorios, PasswordHasherPort hasher,
                                                  AppProperties propiedades, Clock reloj) {
        String email = propiedades.get("admin.email");
        if (repositorios.cuentas().buscarPorEmail(email).isEmpty()) {
            String id = IdGenerator.nuevo("CTA");
            Cuenta admin = new Cuenta(id, email, hasher.hashear(propiedades.get("admin.password")), Rol.ADMIN, id,
                    LocalDateTime.now(reloj));
            admin.setCorreoVerificado(true);
            repositorios.cuentas().guardar(admin);
        }
    }
}
