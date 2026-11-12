package app.configuration;

import app.domain.TarifaParametros;
import app.service.ClienteServiceAdapter;
import app.service.CotizacionServiceAdapter;
import app.service.CuentaServiceAdapter;
import app.service.DocumentoVehiculoServiceAdapter;
import app.service.EmpleadoServiceAdapter;
import app.service.EvaluacionServiceAdapter;
import app.service.FlotaServiceAdapter;
import app.service.LiquidacionServiceAdapter;
import app.service.PicoPlacaServiceAdapter;
import app.service.ProgramacionServiceAdapter;
import app.service.ReservaServiceAdapter;
import app.service.SeguimientoServiceAdapter;
import app.service.helpers.EstrategiaPrecioPorVehiculo;
import app.service.inputports.ClienteServiceInterface;
import app.service.inputports.CotizacionServiceInterface;
import app.service.inputports.CuentaServiceInterface;
import app.service.inputports.DocumentoVehiculoServiceInterface;
import app.service.inputports.EmpleadoServiceInterface;
import app.service.inputports.EvaluacionServiceInterface;
import app.service.inputports.FlotaServiceInterface;
import app.service.inputports.LiquidacionServiceInterface;
import app.service.inputports.PicoPlacaServiceInterface;
import app.service.inputports.ProgramacionServiceInterface;
import app.service.inputports.ReservaServiceInterface;
import app.service.inputports.SeguimientoServiceInterface;
import app.service.outputports.DistanciaPort;
import app.service.outputports.NotificacionPort;
import app.service.outputports.PasarelaPagoPort;
import app.service.outputports.PasswordHasherPort;

import java.time.Clock;

/** Conecta cada caso de uso con los adaptadores que recibe; la UI solo conoce las interfaces. */
public record Servicios(CuentaServiceInterface cuenta, ClienteServiceInterface cliente, FlotaServiceInterface flota,
                        DocumentoVehiculoServiceInterface documentos, PicoPlacaServiceInterface picoPlaca,
                        EmpleadoServiceInterface empleado, CotizacionServiceInterface cotizacion,
                        ReservaServiceInterface reserva, ProgramacionServiceInterface programacion,
                        SeguimientoServiceInterface seguimiento, LiquidacionServiceInterface liquidacion,
                        EvaluacionServiceInterface evaluacion) {

    public static Servicios crear(Repositorios r, PasswordHasherPort hasher, NotificacionPort notificacion,
                                  PasarelaPagoPort pasarela, DistanciaPort distancia, TarifaParametros tarifas,
                                  Clock reloj) {
        return new Servicios(
                new CuentaServiceAdapter(r.cuentas(), r.clientes(), r.tokens(), hasher, notificacion, r.transaccion(), reloj),
                new ClienteServiceAdapter(r.clientes()),
                new FlotaServiceAdapter(r.vehiculos(), r.empleados(), reloj),
                new DocumentoVehiculoServiceAdapter(r.vehiculos(), reloj),
                new PicoPlacaServiceAdapter(r.restricciones(), r.vehiculos()),
                new EmpleadoServiceAdapter(r.empleados(), r.cuentas(), hasher, r.transaccion(), reloj),
                new CotizacionServiceAdapter(r.solicitudes(), distancia, new EstrategiaPrecioPorVehiculo(tarifas), reloj),
                new ReservaServiceAdapter(r.solicitudes(), r.reservas(), r.pagos(), r.eventos(), r.cuentas(), pasarela,
                        notificacion, r.transaccion(), reloj),
                new ProgramacionServiceAdapter(r.reservas(), r.solicitudes(), r.vehiculos(), r.empleados(),
                        r.restricciones(), r.eventos(), r.cuentas(), notificacion, r.transaccion(), reloj),
                new SeguimientoServiceAdapter(r.reservas(), r.solicitudes(), r.eventos(), r.empleados(), r.cuentas(),
                        notificacion, r.transaccion(), reloj),
                new LiquidacionServiceAdapter(r.reservas(), r.pagos(), r.eventos(), r.clientes(), pasarela, notificacion,
                        r.transaccion(), reloj),
                new EvaluacionServiceAdapter(r.evaluaciones(), r.reservas(), r.empleados(), r.transaccion(), reloj));
    }
}
