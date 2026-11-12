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
import app.view.helpers.Pantalla;
import app.view.helpers.Tabla;

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
        Pantalla.subtitulo("Reservas por programar");
        if (pendientes.isEmpty()) {
            Pantalla.info("No hay reservas por programar.");
            return;
        }
        Tabla porProgramar = new Tabla("Reserva", "Solicitado para", "Duración");
        for (Reserva r : pendientes) {
            porProgramar.fila(r.getIdReserva(), Formato.fechaHora(r.getFechaServicio()), r.getDuracionMinutos() + " min");
        }
        porProgramar.imprimir("");
        System.out.println();
        String id = EntradaConsola.texto("Id de la reserva");
        LocalDateTime fecha = EntradaConsola.fechaHora("Fecha y hora definitiva");
        RecursosDisponibles recursos = servicio.consultarRecursos(sesion, id, fecha);

        Pantalla.subtitulo("Vehículos disponibles");
        Tabla vehiculos = new Tabla("Placa", "Tipo", "Volumen", "Conductores habituales");
        for (Vehiculo v : recursos.vehiculos()) {
            vehiculos.fila(v.getPlaca(), v.getTipo().getEtiqueta(), Formato.decimal(v.getCapacidadVolumenM3()) + " m³",
                    Formato.lista(v.getIdsConductores()));
        }
        vehiculos.imprimir("No hay vehículos disponibles.");
        Pantalla.subtitulo("Conductores disponibles");
        empleados(recursos.conductores()).imprimir("No hay conductores disponibles.");
        Pantalla.subtitulo("Auxiliares disponibles (se requieren " + recursos.auxiliaresRequeridos() + ")");
        empleados(recursos.auxiliares()).imprimir("No hay auxiliares disponibles.");
        System.out.println();

        String placa = EntradaConsola.texto("Placa");
        String conductor = EntradaConsola.texto("Id del conductor");
        List<String> auxiliares = new ArrayList<>();
        for (int i = 1; i <= recursos.auxiliaresRequeridos(); i++) {
            auxiliares.add(EntradaConsola.texto("Id del auxiliar " + i));
        }
        Reserva reserva = servicio.programar(sesion, new ProgramacionServicio(id, fecha, placa, conductor, auxiliares));
        Pantalla.exito("Reserva " + reserva.getIdReserva() + ": " + reserva.getEstado().getEtiqueta());
    }

    public void agenda(Sesion sesion) {
        Pantalla.subtitulo("Agenda de mudanzas");
        LocalDate desde = EntradaConsola.fecha("Desde");
        LocalDate hasta = EntradaConsola.fecha("Hasta");
        List<ItemAgenda> agenda = servicio.consultarAgenda(sesion, desde, hasta);
        System.out.println();
        Tabla tabla = new Tabla("Inicio", "Fin", "Reserva", "Placa", "Estado", "Ruta", "Conductor", "Auxiliares");
        for (ItemAgenda i : agenda) {
            tabla.fila(Formato.fechaHora(i.inicio()), Formato.fechaHora(i.fin()), i.idReserva(), i.placa(),
                    i.estado().getEtiqueta(), i.origen() + " → " + i.destino(), i.conductor(),
                    Formato.lista(i.auxiliares()));
        }
        tabla.imprimir("No hay mudanzas programadas en ese rango.");
    }

    private static Tabla empleados(List<Empleado> empleados) {
        Tabla tabla = new Tabla("Id", "Nombre");
        empleados.forEach(e -> tabla.fila(e.getIdEmpleado(), e.getNombre()));
        return tabla;
    }
}
