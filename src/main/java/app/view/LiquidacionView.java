package app.view;

import app.domain.Pago;
import app.domain.Sesion;
import app.domain.enums.MetodoPago;
import app.service.inputports.LiquidacionServiceInterface;
import app.service.inputports.dto.Factura;
import app.view.helpers.EntradaConsola;
import app.view.helpers.Ficha;
import app.view.helpers.Formato;
import app.view.helpers.Pantalla;
import app.view.helpers.Tabla;

public class LiquidacionView {

    private static final MetodoPago[] METODOS_DIGITALES = {MetodoPago.TARJETA, MetodoPago.TRANSFERENCIA};

    private final LiquidacionServiceInterface servicio;

    public LiquidacionView(LiquidacionServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void pagarSaldo(Sesion sesion) {
        Pantalla.subtitulo("Pagar saldo");
        String id = EntradaConsola.texto("Id de la reserva");
        Pantalla.info("Saldo pendiente: " + Formato.pesos(servicio.consultarSaldo(sesion, id)));
        MetodoPago metodo = EntradaConsola.opcion("Método de pago", METODOS_DIGITALES, MetodoPago::getEtiqueta);
        String token = EntradaConsola.texto("Token de pago de la pasarela de pruebas (tok_aprobado o tok_rechazado)");
        mostrar(servicio.pagarSaldoDigital(sesion, id, metodo, token));
    }

    public void registrarEfectivo(Sesion sesion) {
        Pantalla.subtitulo("Registrar pago en efectivo");
        String id = EntradaConsola.texto("Id de la reserva");
        Pantalla.info("Saldo a recibir: " + Formato.pesos(servicio.consultarSaldo(sesion, id)));
        if (EntradaConsola.siNo("¿Confirma que recibió el efectivo?")) {
            mostrar(servicio.registrarSaldoEfectivo(sesion, id));
        }
    }

    public void verFactura(Sesion sesion) {
        mostrar(servicio.consultarFactura(sesion, EntradaConsola.texto("Id de la reserva")));
    }

    private void mostrar(Factura f) {
        new Ficha("Recibo digital " + f.numero())
                .campo("Cliente", f.cliente())
                .campo("Fecha", Formato.fechaHora(f.fecha()))
                .campo("Total", Formato.pesos(f.total()))
                .imprimir();
        Pantalla.subtitulo("Pagos");
        Tabla tabla = new Tabla("Tipo", "Método", "Monto", "Referencia");
        for (Pago p : f.pagos()) {
            tabla.fila(p.getTipoPago().getEtiqueta(), p.getMetodoPago().getEtiqueta(), Formato.pesos(p.getMonto()),
                    p.getReferencia());
        }
        tabla.imprimir("Sin pagos registrados.");
    }
}
