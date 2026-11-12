package app.view;

import app.domain.Empleado;
import app.domain.Reserva;
import app.domain.Sesion;
import app.domain.Vehiculo;
import app.service.inputports.ProgramacionServiceInterface;
import app.service.inputports.dto.ItemAgenda;
import app.service.inputports.dto.ProgramacionServicio;
import app.service.inputports.dto.RecursosDisponibles;
import app.view.helpers.EntradaConsola;
import app.view.helpers.Formato;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProgramacionView {

    private final ProgramacionServiceInterface servicio;

    public ProgramacionView(ProgramacionServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void programar(Sesion sesion) {
        List<Reserva> pendientes = servicio.listarPorProgramar(sesion);
        if (pendientes.isEmpty()) {
            System.out.println("No hay reservas por programar.");
            return;
        }
        for (Reserva r : pendientes) {
            System.out.println(r.getIdReserva() + " | Solicitado para " + Formato.fechaHora(r.getFechaServicio())
                    + " | Duración " + r.getDuracionMinutos() + " min");
        }
        String id = EntradaConsola.texto("Id de la reserva");
        LocalDateTime fecha = EntradaConsola.fechaHora("Fecha y hora definitiva");
        RecursosDisponibles recursos = servicio.consultarRecursos(sesion, id, fecha);

        System.out.println("Vehículos disponibles:");
        for (Vehiculo v : recursos.vehiculos()) {
            System.out.println("  " + v.getPlaca() + " | " + v.getTipo().getEtiqueta() + " | " + v.getCapacidadVolumenM3()
                    + " m³ | Conductores habituales: " + v.getIdsConductores());
        }
        System.out.println("Conductores disponibles:");
        recursos.conductores().forEach(ProgramacionView::mostrarEmpleado);
        System.out.println("Auxiliares disponibles (se requieren " + recursos.auxiliaresRequeridos() + "):");
        recursos.auxiliares().forEach(ProgramacionView::mostrarEmpleado);

        String placa = EntradaConsola.texto("Placa");
        String conductor = EntradaConsola.texto("Id del conductor");
        List<String> auxiliares = new ArrayList<>();
        for (int i = 1; i <= recursos.auxiliaresRequeridos(); i++) {
            auxiliares.add(EntradaConsola.texto("Id del auxiliar " + i));
        }
        Reserva reserva = servicio.programar(sesion, new ProgramacionServicio(id, fecha, placa, conductor, auxiliares));
        System.out.println("Reserva " + reserva.getIdReserva() + ": " + reserva.getEstado().getEtiqueta());
    }

    public void agenda(Sesion sesion) {
        LocalDate desde = EntradaConsola.fecha("Desde");
        LocalDate hasta = EntradaConsola.fecha("Hasta");
        List<ItemAgenda> agenda = servicio.consultarAgenda(sesion, desde, hasta);
        if (agenda.isEmpty()) {
            System.out.println("No hay mudanzas programadas en ese rango.");
        }
        for (ItemAgenda i : agenda) {
            System.out.println(Formato.fechaHora(i.inicio()) + " a " + Formato.fechaHora(i.fin()) + " | " + i.placa()
                    + " | " + i.estado().getEtiqueta() + " | " + i.origen() + " → " + i.destino() + " | Conductor: "
                    + i.conductor() + " | Auxiliares: " + i.auxiliares() + " | " + i.idReserva());
        }
    }

    private static void mostrarEmpleado(Empleado e) {
        System.out.println("  " + e.getIdEmpleado() + " | " + e.getNombre());
    }
}
