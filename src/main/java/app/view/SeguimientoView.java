package app.view;

import app.domain.EventoServicio;
import app.domain.Sesion;
import app.service.inputports.SeguimientoServiceInterface;
import app.service.inputports.dto.EstadoServicio;
import app.service.inputports.dto.ServicioAsignado;
import app.view.helpers.EntradaConsola;
import app.view.helpers.Formato;

import java.util.List;

public class SeguimientoView {

    private final SeguimientoServiceInterface servicio;

    public SeguimientoView(SeguimientoServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void consultarEstado(Sesion sesion) {
        mostrar(servicio.consultarEstado(sesion, EntradaConsola.texto("Id de la reserva")));
    }

    public void misServicios(Sesion sesion) {
        List<ServicioAsignado> servicios = servicio.listarMisServicios(sesion);
        if (servicios.isEmpty()) {
            System.out.println("No tiene servicios asignados.");
        }
        for (ServicioAsignado s : servicios) {
            System.out.println(s.idReserva() + " | " + Formato.fechaHora(s.inicio()) + " | " + s.estado().getEtiqueta()
                    + " | " + s.placa() + " | " + s.origen() + " → " + s.destino());
        }
    }

    public void avanzarHito(Sesion sesion) {
        mostrar(servicio.avanzarHito(sesion, EntradaConsola.texto("Id de la reserva")));
    }

    private void mostrar(EstadoServicio e) {
        System.out.println("Servicio " + e.idReserva() + ": " + e.etapa() + " (" + e.detalle().getEtiqueta() + ")"
                + "\nFecha: " + Formato.fechaHora(e.fechaServicio()) + " | Vehículo: " + Formato.texto(e.placa())
                + " | Conductor: " + Formato.texto(e.conductor()) + "\nHistorial:");
        for (EventoServicio evento : e.historial()) {
            System.out.println("  " + Formato.fechaHora(evento.fechaHora()) + " - " + evento.estado().getEtiqueta());
        }
    }
}
