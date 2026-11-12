package app.service;

import app.configuration.Repositorios;
import app.configuration.Servicios;
import app.domain.Empleado;
import app.domain.Reserva;
import app.domain.Sesion;
import app.domain.SolicitudServicio;
import app.domain.TarifaParametros;
import app.domain.Ubicacion;
import app.domain.Vehiculo;
import app.domain.enums.CargoEmpleado;
import app.domain.enums.Municipio;
import app.domain.enums.MetodoPago;
import app.domain.enums.Rol;
import app.domain.enums.TipoCarga;
import app.domain.enums.TipoServicio;
import app.domain.enums.TipoVehiculo;
import app.persistence.external.DistanciaMunicipiosAdapter;
import app.persistence.external.PasarelaPagoSimuladaAdapter;
import app.persistence.external.Pbkdf2PasswordHasherAdapter;
import app.service.inputports.dto.ProgramacionServicio;
import app.service.inputports.dto.RegistroEmpleado;
import app.service.inputports.dto.RegistroVehiculo;
import app.service.inputports.dto.SolicitudCotizacion;
import app.service.outputports.NotificacionPort;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Arma el sistema completo para las pruebas. Por defecto usa los adaptadores en memoria;
 * con la variable HW_TEST_DB_URL las mismas pruebas corren contra MySQL.
 */
class Entorno {

    static final ZoneId ZONA = ZoneId.of("America/Bogota");
    static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 2, 8, 0);
    /** Martes futuro, sin restricciones salvo que la prueba las cree. */
    static final LocalDateTime FECHA_SERVICIO = LocalDateTime.of(2026, 11, 10, 9, 0);
    static final String CLAVE = "Clave123*";
    private static final String[] TABLAS = {"evaluacion", "evento_servicio", "pago", "reserva_auxiliar", "reserva",
            "solicitud_servicio", "restriccion_pico_placa", "vehiculo_conductor", "vehiculo", "empleado", "cliente",
            "token_cuenta", "cuenta"};
    private static Connection conexion;

    final RelojMovible reloj = new RelojMovible();
    final List<Correo> correos = new ArrayList<>();
    final Repositorios repos = repositorios();
    final Servicios s = Servicios.crear(repos, new Pbkdf2PasswordHasherAdapter(),
            (NotificacionPort) (d, a, m) -> correos.add(new Correo(d, a, m)), new PasarelaPagoSimuladaAdapter(),
            new DistanciaMunicipiosAdapter(), tarifas(), reloj);
    final Sesion admin = new Sesion("CTA-ADMIN", "CTA-ADMIN", "admin@hw.com", Rol.ADMIN);
    private int consecutivo;

    record Correo(String destinatario, String asunto, String mensaje) {
    }

    static class RelojMovible extends Clock {
        private Instant instante = AHORA.atZone(ZONA).toInstant();

        void avanzar(Duration duracion) {
            instante = instante.plus(duracion);
        }

        @Override
        public ZoneId getZone() {
            return ZONA;
        }

        @Override
        public Clock withZone(ZoneId zona) {
            return this;
        }

        @Override
        public Instant instant() {
            return instante;
        }
    }

    private static Repositorios repositorios() {
        String url = System.getenv("HW_TEST_DB_URL");
        if (url == null) {
            return Repositorios.enMemoria();
        }
        try {
            if (conexion == null) {
                conexion = DriverManager.getConnection(url, System.getenv("HW_TEST_DB_USER"), System.getenv("HW_TEST_DB_PASSWORD"));
            }
            try (Statement st = conexion.createStatement()) {
                for (String tabla : TABLAS) {
                    st.executeUpdate("DELETE FROM " + tabla);
                }
            }
            return Repositorios.enBaseDeDatos(conexion);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    static TarifaParametros tarifas() {
        Map<TipoVehiculo, BigDecimal> base = new EnumMap<>(TipoVehiculo.class);
        base.put(TipoVehiculo.MOTOCARRO, new BigDecimal("60000"));
        base.put(TipoVehiculo.LUV, new BigDecimal("120000"));
        base.put(TipoVehiculo.NPR, new BigDecimal("220000"));
        Map<TipoVehiculo, BigDecimal> km = new EnumMap<>(TipoVehiculo.class);
        km.put(TipoVehiculo.MOTOCARRO, new BigDecimal("1500"));
        km.put(TipoVehiculo.LUV, new BigDecimal("2500"));
        km.put(TipoVehiculo.NPR, new BigDecimal("4000"));
        Map<TipoVehiculo, Double> m3 = new EnumMap<>(TipoVehiculo.class);
        m3.put(TipoVehiculo.MOTOCARRO, 2.0);
        m3.put(TipoVehiculo.LUV, 12.0);
        m3.put(TipoVehiculo.NPR, 28.0);
        return new TarifaParametros(base, km, m3, new BigDecimal("12000"), new BigDecimal("70000"), 0.85);
    }

    Sesion cliente() {
        String email = "cliente" + (++consecutivo) + "@correo.com";
        s.cuenta().registrarCliente(email, CLAVE, true);
        return s.cuenta().iniciarSesion(email, CLAVE);
    }

    Vehiculo vehiculo(String placa, double m3) {
        return s.flota().registrarVehiculo(admin, new RegistroVehiculo(placa, TipoVehiculo.LUV, "2022", 2000, m3,
                LocalDate.of(2027, 6, 1), LocalDate.of(2027, 6, 1)));
    }

    Empleado conductor() {
        return empleado(CargoEmpleado.CONDUCTOR, "C2", LocalDate.of(2028, 1, 1));
    }

    Empleado auxiliar() {
        return empleado(CargoEmpleado.AUXILIAR_CARGA, null, null);
    }

    Empleado empleado(CargoEmpleado cargo, String licencia, LocalDate vencimiento) {
        int n = ++consecutivo;
        return s.empleado().registrarEmpleado(admin, new RegistroEmpleado("Empleado " + n, "10" + n, "3001234567",
                "empleado" + n + "@hw.com", CLAVE, cargo, "Mañana", 30, "M", "Soltero", "Sura", licencia, vencimiento, "L"));
    }

    Sesion sesionDe(Empleado empleado) {
        return s.cuenta().iniciarSesion(empleado.getEmail(), CLAVE);
    }

    SolicitudCotizacion datosCotizacion(int ayudantes, double m3) {
        return new SolicitudCotizacion(TipoServicio.MUDANZA, FECHA_SERVICIO.toLocalDate(),
                new Ubicacion(Municipio.MEDELLIN, "Calle 10 # 20-30", 3, false),
                new Ubicacion(Municipio.ENVIGADO, "Carrera 43 # 5-10", 1, true), m3, Map.of(), TipoCarga.MEDIA, ayudantes);
    }

    SolicitudServicio cotizacionAceptada(Sesion cliente, int ayudantes, double m3) {
        SolicitudServicio solicitud = s.cotizacion().cotizar(cliente, datosCotizacion(ayudantes, m3));
        return s.cotizacion().aceptar(cliente, solicitud.getIdSolicitud());
    }

    Reserva reservaPagada(Sesion cliente, int ayudantes, double m3) {
        SolicitudServicio solicitud = cotizacionAceptada(cliente, ayudantes, m3);
        return s.reserva().reservar(cliente, solicitud.getIdSolicitud(), FECHA_SERVICIO, MetodoPago.TARJETA, null,
                "tok_aprobado").reserva();
    }

    /** Lleva un servicio hasta Confirmado, con un conductor y sin auxiliares. */
    Programado servicioProgramado() {
        Sesion cliente = cliente();
        Reserva reserva = reservaPagada(cliente, 0, 5);
        Empleado conductor = conductor();
        Vehiculo vehiculo = vehiculo("ABC12" + (consecutivo % 10), 12);
        s.programacion().programar(admin, new ProgramacionServicio(reserva.getIdReserva(), FECHA_SERVICIO,
                vehiculo.getPlaca(), conductor.getIdEmpleado(), List.of()));
        return new Programado(cliente, sesionDe(conductor), conductor, vehiculo, reserva.getIdReserva());
    }

    record Programado(Sesion cliente, Sesion conductorSesion, Empleado conductor, Vehiculo vehiculo, String idReserva) {
    }

    Programado servicioEnDestino() {
        Programado p = servicioProgramado();
        for (int i = 0; i < 3; i++) {
            s.seguimiento().avanzarHito(p.conductorSesion(), p.idReserva());
        }
        return p;
    }

    String ultimoToken() {
        String mensaje = correos.get(correos.size() - 1).mensaje();
        if (mensaje.contains("token=")) {
            return mensaje.substring(mensaje.indexOf("token=") + 6).trim();
        }
        return mensaje.substring(mensaje.indexOf(": ") + 2, mensaje.indexOf(" (vigente"));
    }
}
