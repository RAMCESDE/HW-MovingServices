package app.view;

import app.domain.EventoServicio;
import app.domain.Sesion;
import app.service.inputports.SeguimientoServiceInterface;
import app.service.inputports.dto.EstadoServicio;
import app.service.inputports.dto.ServicioAsignado;
import app.view.helpers.EntradaConsola;
import app.view.helpers.Ficha;
import app.view.helpers.Formato;
import app.view.helpers.Pantalla;
import app.view.helpers.Tabla;

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
        Pantalla.subtitulo("Mis servicios");
        Tabla tabla = new Tabla("Reserva", "Inicio", "Estado", "Placa", "Ruta");
        for (ServicioAsignado s : servicios) {
            tabla.fila(s.idReserva(), Formato.fechaHora(s.inicio()), s.estado().getEtiqueta(), s.placa(),
                    s.origen() + " → " + s.destino());
        }
        tabla.imprimir("No tiene servicios asignados.");
    }

    public void avanzarHito(Sesion sesion) {
        mostrar(servicio.avanzarHito(sesion, EntradaConsola.texto("Id de la reserva")));
    }

    private void mostrar(EstadoServicio e) {
        new Ficha("Servicio " + e.idReserva())
                .campo("Etapa", e.etapa() + " (" + e.detalle().getEtiqueta() + ")")
                .campo("Fecha", Formato.fechaHora(e.fechaServicio()))
                .campo("Vehículo", e.placa())
                .campo("Conductor", e.conductor())
                .imprimir();
        Pantalla.subtitulo("Historial");
        Tabla tabla = new Tabla("Fecha", "Estado");
        for (EventoServicio evento : e.historial()) {
            tabla.fila(Formato.fechaHora(evento.fechaHora()), evento.estado().getEtiqueta());
        }
        tabla.imprimir("Sin eventos registrados.");
    }
}
