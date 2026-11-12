package app.view;

import app.domain.Pago;
import app.domain.Sesion;
import app.domain.enums.MetodoPago;
import app.service.inputports.LiquidacionServiceInterface;
import app.service.inputports.dto.Factura;
import app.view.helpers.EntradaConsola;
import app.view.helpers.Formato;

public class LiquidacionView {

    private static final MetodoPago[] METODOS_DIGITALES = {MetodoPago.TARJETA, MetodoPago.TRANSFERENCIA};

    private final LiquidacionServiceInterface servicio;

    public LiquidacionView(LiquidacionServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void pagarSaldo(Sesion sesion) {
        String id = EntradaConsola.texto("Id de la reserva");
        System.out.println("Saldo pendiente: " + Formato.pesos(servicio.consultarSaldo(sesion, id)));
        MetodoPago metodo = EntradaConsola.opcion("Método de pago", METODOS_DIGITALES, MetodoPago::getEtiqueta);
        String token = EntradaConsola.texto("Token de pago de la pasarela de pruebas (tok_aprobado o tok_rechazado)");
        mostrar(servicio.pagarSaldoDigital(sesion, id, metodo, token));
    }

    public void registrarEfectivo(Sesion sesion) {
        String id = EntradaConsola.texto("Id de la reserva");
        System.out.println("Saldo a recibir: " + Formato.pesos(servicio.consultarSaldo(sesion, id)));
        if (EntradaConsola.siNo("¿Confirma que recibió el efectivo?")) {
            mostrar(servicio.registrarSaldoEfectivo(sesion, id));
        }
    }

    public void verFactura(Sesion sesion) {
        mostrar(servicio.consultarFactura(sesion, EntradaConsola.texto("Id de la reserva")));
    }

    private void mostrar(Factura f) {
        System.out.println("\nRECIBO DIGITAL " + f.numero() + "\nCliente: " + f.cliente() + "\nFecha: "
                + Formato.fechaHora(f.fecha()) + "\nTotal: " + Formato.pesos(f.total()));
        for (Pago p : f.pagos()) {
            System.out.println("  " + p.getTipoPago().getEtiqueta() + " | " + p.getMetodoPago().getEtiqueta() + " | "
                    + Formato.pesos(p.getMonto()) + " | " + p.getReferencia());
        }
    }
}
