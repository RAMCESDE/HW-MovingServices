package app.configuration;

import app.domain.Cuenta;
import app.domain.TarifaParametros;
import app.domain.enums.Rol;
import app.domain.enums.TipoVehiculo;
import app.persistence.dbconnection.DataBaseConnection;
import app.persistence.external.DistanciaMunicipiosAdapter;
import app.persistence.external.NotificacionConsolaAdapter;
import app.persistence.external.PasarelaPagoSimuladaAdapter;
import app.persistence.external.Pbkdf2PasswordHasherAdapter;
import app.service.helpers.IdGenerator;
import app.service.outputports.PasswordHasherPort;
import app.ui.CliUserInterface;
import app.view.ClienteView;
import app.view.CotizacionView;
import app.view.CuentaView;
import app.view.DocumentoVehiculoView;
import app.view.EmpleadoView;
import app.view.EvaluacionView;
import app.view.FlotaView;
import app.view.LiquidacionView;
import app.view.PicoPlacaView;
import app.view.ProgramacionView;
import app.view.ReservaView;
import app.view.SeguimientoView;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/** Raíz de composición: la única clase que conoce las implementaciones concretas. */
public class Config {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    public static CliUserInterface getCliUserInterface() {
        AppProperties propiedades = new AppProperties();
        Clock reloj = Clock.system(ZONA);
        PasswordHasherPort hasher = new Pbkdf2PasswordHasherAdapter();

        Repositorios repositorios = crearRepositorios(propiedades);
        crearAdministradorInicial(repositorios, hasher, propiedades, reloj);

        Servicios s = Servicios.crear(repositorios, hasher, new NotificacionConsolaAdapter(),
                new PasarelaPagoSimuladaAdapter(), new DistanciaMunicipiosAdapter(), leerTarifas(propiedades), reloj);

        return new CliUserInterface(new CuentaView(s.cuenta()), new ClienteView(s.cliente()), new FlotaView(s.flota()),
                new DocumentoVehiculoView(s.documentos()), new PicoPlacaView(s.picoPlaca()),
                new EmpleadoView(s.empleado()), new CotizacionView(s.cotizacion()), new ReservaView(s.reserva()),
                new ProgramacionView(s.programacion()), new SeguimientoView(s.seguimiento()),
                new LiquidacionView(s.liquidacion()), new EvaluacionView(s.evaluacion()));
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

    private static TarifaParametros leerTarifas(AppProperties p) {
        return new TarifaParametros(porVehiculo(p, "tarifa.base.", BigDecimal::new),
                porVehiculo(p, "tarifa.km.", BigDecimal::new), porVehiculo(p, "capacidad.m3.", Double::valueOf),
                new BigDecimal(p.get("tarifa.piso")), new BigDecimal(p.get("tarifa.ayudante")),
                Double.parseDouble(p.get("tarifa.factor.acarreo")));
    }

    private static <T> Map<TipoVehiculo, T> porVehiculo(AppProperties p, String prefijo, Function<String, T> conversor) {
        Map<TipoVehiculo, T> valores = new EnumMap<>(TipoVehiculo.class);
        for (TipoVehiculo tipo : TipoVehiculo.values()) {
            valores.put(tipo, conversor.apply(p.get(prefijo + tipo.name())));
        }
        return valores;
    }
}
