package app.ui;

import app.domain.Sesion;
import app.domain.exceptions.AccesoDenegadoException;
import app.domain.exceptions.DatoInvalidoException;
import app.domain.exceptions.PersistenciaException;
import app.domain.exceptions.RecursoNoEncontradoException;
import app.domain.exceptions.ReglaNegocioException;
import app.view.ClienteView;
import app.view.CuentaView;
import app.view.helpers.EntradaConsola;

import java.util.NoSuchElementException;

public class CliUserInterface {

    private final CuentaView cuentaView;
    private final ClienteView clienteView;

    public CliUserInterface(CuentaView cuentaView,
                            ClienteView clienteView) {
        this.cuentaView = cuentaView;
        this.clienteView = clienteView;
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
            default -> System.out.println("Este rol tendrá opciones en las siguientes épicas.");
        }
    }

    private void menuCliente(Sesion s) {
        while (true) {
            int opcion = EntradaConsola.entero("""

                    === CLIENTE ===
                    1. Ver mi perfil
                    2. Editar mi perfil
                    0. Cerrar sesión
                    Seleccione""");
            switch (opcion) {
                case 1 -> ejecutar(() -> clienteView.verPerfil(s));
                case 2 -> ejecutar(() -> clienteView.editarPerfil(s));
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
                 | AccesoDenegadoException | PersistenciaException e) {
            System.out.println("No se pudo completar: " + e.getMessage());
        }
    }
}
