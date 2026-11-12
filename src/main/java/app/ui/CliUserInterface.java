package app.ui;

import app.domain.Sesion;
import app.domain.exceptions.AccesoDenegadoException;
import app.domain.exceptions.DatoInvalidoException;
import app.domain.exceptions.PagoRechazadoException;
import app.domain.exceptions.PersistenciaException;
import app.domain.exceptions.RecursoNoEncontradoException;
import app.domain.exceptions.ReglaNegocioException;
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
import app.view.helpers.EntradaConsola;

import java.util.NoSuchElementException;

public class CliUserInterface {

    private final CuentaView cuentaView;
    private final ClienteView clienteView;
    private final FlotaView flotaView;
    private final DocumentoVehiculoView documentoView;
    private final PicoPlacaView picoPlacaView;
    private final EmpleadoView empleadoView;
    private final CotizacionView cotizacionView;
    private final ReservaView reservaView;
    private final ProgramacionView programacionView;
    private final SeguimientoView seguimientoView;
    private final LiquidacionView liquidacionView;
    private final EvaluacionView evaluacionView;

    public CliUserInterface(CuentaView cuentaView, ClienteView clienteView, FlotaView flotaView,
                            DocumentoVehiculoView documentoView, PicoPlacaView picoPlacaView,
                            EmpleadoView empleadoView, CotizacionView cotizacionView, ReservaView reservaView,
                            ProgramacionView programacionView, SeguimientoView seguimientoView,
                            LiquidacionView liquidacionView, EvaluacionView evaluacionView) {
        this.cuentaView = cuentaView;
        this.clienteView = clienteView;
        this.flotaView = flotaView;
        this.documentoView = documentoView;
        this.picoPlacaView = picoPlacaView;
        this.empleadoView = empleadoView;
        this.cotizacionView = cotizacionView;
        this.reservaView = reservaView;
        this.programacionView = programacionView;
        this.seguimientoView = seguimientoView;
        this.liquidacionView = liquidacionView;
        this.evaluacionView = evaluacionView;
    }

    public void applicationInit() {
        System.out.println("Bienvenido a HW Moving Services");
        try {
            menuInicial();
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada finalizada.");
        }
        System.out.println("Saliendo de la aplicación");
    }

    private void menuInicial() {
        while (true) {
            int opcion = EntradaConsola.entero("""

                    === INICIO ===
                    1. Registrarme como cliente
                    2. Iniciar sesión
                    3. Verificar correo
                    4. Olvidé mi contraseña
                    5. Restablecer contraseña con token
                    0. Salir
                    Seleccione""");
            switch (opcion) {
                case 1 -> ejecutar(cuentaView::registrarCliente);
                case 2 -> ejecutar(this::iniciarSesion);
                case 3 -> ejecutar(cuentaView::verificarCorreo);
                case 4 -> ejecutar(cuentaView::solicitarRecuperacion);
                case 5 -> ejecutar(cuentaView::restablecerContrasena);
                case 0 -> {
                    return;
                }
                default -> System.out.println("Seleccione una opción válida");
            }
        }
    }

    private void iniciarSesion() {
        Sesion sesion = cuentaView.iniciarSesion();
        switch (sesion.rol()) {
            case CLIENTE -> menuCliente(sesion);
            case ADMIN -> menuAdministrador(sesion);
            case CONDUCTOR -> menuConductor(sesion);
            case AUXILIAR_CARGA -> menuAuxiliar(sesion);
        }
    }

    private void menuCliente(Sesion s) {
        while (true) {
            int opcion = EntradaConsola.entero("""

                    === CLIENTE ===
                    1. Ver mi perfil
                    2. Editar mi perfil
                    3. Solicitar cotización
                    4. Ver mis cotizaciones
                    5. Aceptar, rechazar o guardar una cotización
                    6. Reservar y pagar anticipo
                    7. Ver mis reservas
                    8. Ver estado de mi mudanza
                    9. Pagar saldo
                    10. Ver recibo
                    11. Calificar servicio
                    0. Cerrar sesión
                    Seleccione""");
            switch (opcion) {
                case 1 -> ejecutar(() -> clienteView.verPerfil(s));
                case 2 -> ejecutar(() -> clienteView.editarPerfil(s));
                case 3 -> ejecutar(() -> cotizacionView.solicitar(s));
                case 4 -> ejecutar(() -> cotizacionView.listar(s));
                case 5 -> ejecutar(() -> cotizacionView.retomar(s));
                case 6 -> ejecutar(() -> reservaView.reservar(s));
                case 7 -> ejecutar(() -> reservaView.listarMias(s));
                case 8 -> ejecutar(() -> seguimientoView.consultarEstado(s));
                case 9 -> ejecutar(() -> liquidacionView.pagarSaldo(s));
                case 10 -> ejecutar(() -> liquidacionView.verFactura(s));
                case 11 -> ejecutar(() -> evaluacionView.calificar(s));
                case 0 -> {
                    return;
                }
                default -> System.out.println("Seleccione una opción válida");
            }
        }
    }

    private void menuAdministrador(Sesion s) {
        ejecutar(() -> documentoView.mostrarAlertas(s));
        while (true) {
            int opcion = EntradaConsola.entero("""

                    === ADMINISTRADOR ===
                    1. Registrar vehículo
                    2. Ver flota
                    3. Asignar conductor a vehículo
                    4. Retirar conductor de vehículo
                    5. Actualizar documentos de vehículo
                    6. Ver alertas de documentos
                    7. Crear restricción de Pico y Placa
                    8. Ver restricciones de Pico y Placa
                    9. Eliminar restricción de Pico y Placa
                    10. Consultar Pico y Placa de un vehículo
                    11. Registrar empleado
                    12. Ver empleados
                    13. Cambiar estado de empleado
                    14. Registrar anticipo en efectivo
                    15. Programar servicio
                    16. Ver agenda
                    17. Consultar estado de un servicio
                    18. Ver métricas de calidad
                    0. Cerrar sesión
                    Seleccione""");
            switch (opcion) {
                case 1 -> ejecutar(() -> flotaView.registrar(s));
                case 2 -> ejecutar(() -> flotaView.listar(s));
                case 3 -> ejecutar(() -> flotaView.asignarConductor(s));
                case 4 -> ejecutar(() -> flotaView.retirarConductor(s));
                case 5 -> ejecutar(() -> documentoView.actualizar(s));
                case 6 -> ejecutar(() -> documentoView.mostrarAlertas(s));
                case 7 -> ejecutar(() -> picoPlacaView.crear(s));
                case 8 -> ejecutar(() -> picoPlacaView.listar(s));
                case 9 -> ejecutar(() -> picoPlacaView.eliminar(s));
                case 10 -> ejecutar(() -> picoPlacaView.consultarVehiculo(s));
                case 11 -> ejecutar(() -> empleadoView.registrar(s));
                case 12 -> ejecutar(() -> empleadoView.listar(s));
                case 13 -> ejecutar(() -> empleadoView.cambiarEstado(s));
                case 14 -> ejecutar(() -> reservaView.registrarAnticipoEfectivo(s));
                case 15 -> ejecutar(() -> programacionView.programar(s));
                case 16 -> ejecutar(() -> programacionView.agenda(s));
                case 17 -> ejecutar(() -> seguimientoView.consultarEstado(s));
                case 18 -> ejecutar(() -> evaluacionView.metricas(s));
                case 0 -> {
                    return;
                }
                default -> System.out.println("Seleccione una opción válida");
            }
        }
    }

    private void menuConductor(Sesion s) {
        while (true) {
            int opcion = EntradaConsola.entero("""

                    === CONDUCTOR ===
                    1. Ver mis servicios
                    2. Registrar siguiente hito de ruta
                    3. Registrar pago del saldo en efectivo
                    0. Cerrar sesión
                    Seleccione""");
            switch (opcion) {
                case 1 -> ejecutar(() -> seguimientoView.misServicios(s));
                case 2 -> ejecutar(() -> seguimientoView.avanzarHito(s));
                case 3 -> ejecutar(() -> liquidacionView.registrarEfectivo(s));
                case 0 -> {
                    return;
                }
                default -> System.out.println("Seleccione una opción válida");
            }
        }
    }

    private void menuAuxiliar(Sesion s) {
        while (true) {
            int opcion = EntradaConsola.entero("""

                    === AUXILIAR DE CARGA ===
                    1. Ver mis servicios
                    0. Cerrar sesión
                    Seleccione""");
            switch (opcion) {
                case 1 -> ejecutar(() -> seguimientoView.misServicios(s));
                case 0 -> {
                    return;
                }
                default -> System.out.println("Seleccione una opción válida");
            }
        }
    }

    /** Un error de negocio se informa al usuario sin cerrar la aplicación. */
    private void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (DatoInvalidoException | ReglaNegocioException | RecursoNoEncontradoException
                 | AccesoDenegadoException | PagoRechazadoException | PersistenciaException e) {
            System.out.println("No se pudo completar: " + e.getMessage());
        }
    }
}
