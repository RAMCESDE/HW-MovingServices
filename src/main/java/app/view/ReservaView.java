package app.view;

import app.domain.Reserva;
import app.domain.Sesion;
import app.domain.enums.MetodoPago;
import app.service.inputports.ReservaServiceInterface;
import app.service.inputports.dto.ResultadoReserva;
import app.view.helpers.EntradaConsola;
import app.view.helpers.Formato;
import app.view.helpers.Pantalla;
import app.view.helpers.Tabla;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReservaView {

    private final ReservaServiceInterface servicio;

    public ReservaView(ReservaServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void reservar(Sesion sesion) {
        Pantalla.subtitulo("Reservar servicio");
        String id = EntradaConsola.texto("Id de la cotización aceptada");
        LocalDateTime fecha = EntradaConsola.fechaHora("Fecha y hora del servicio");
        MetodoPago metodo = EntradaConsola.opcion("Método de pago del anticipo", MetodoPago.values(), MetodoPago::getEtiqueta);
        BigDecimal anticipo = EntradaConsola.dineroOpcional("Valor del anticipo (vacío = 10% mínimo)");
        String token = metodo == MetodoPago.EFECTIVO ? null
                : EntradaConsola.texto("Token de pago de la pasarela de pruebas (tok_aprobado o tok_rechazado)");
        ResultadoReserva resultado = servicio.reservar(sesion, id, fecha, metodo, anticipo, token);
        Pantalla.recuadro(resultado.comprobante());
    }

    public void listarMias(Sesion sesion) {
        Pantalla.subtitulo("Mis reservas");
        mostrar(servicio.listarMisReservas(sesion));
    }

    public void registrarAnticipoEfectivo(Sesion sesion) {
        List<Reserva> pendientes = servicio.listarAnticiposPendientes(sesion);
        Pantalla.subtitulo("Anticipos pendientes en efectivo");
        mostrar(pendientes);
        if (!pendientes.isEmpty()) {
            System.out.println();
            ResultadoReserva resultado = servicio.registrarAnticipoEfectivo(sesion, EntradaConsola.texto("Id de la reserva"));
            Pantalla.exito("Reserva " + resultado.reserva().getIdReserva() + ": "
                    + resultado.reserva().getEstado().getEtiqueta());
        }
    }

    private void mostrar(List<Reserva> reservas) {
        Tabla tabla = new Tabla("Reserva", "Fecha del servicio", "Estado", "Total");
        for (Reserva r : reservas) {
            tabla.fila(r.getIdReserva(), Formato.fechaHora(r.getFechaServicio()), r.getEstado().getEtiqueta(),
                    Formato.pesos(r.getTotal()));
        }
        tabla.imprimir("No hay reservas.");
    }
}
