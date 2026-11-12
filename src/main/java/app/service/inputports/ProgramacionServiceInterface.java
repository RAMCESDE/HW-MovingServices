package app.service.inputports;

import app.domain.Reserva;
import app.domain.Sesion;
import app.service.inputports.dto.ItemAgenda;
import app.service.inputports.dto.ProgramacionServicio;
import app.service.inputports.dto.RecursosDisponibles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ProgramacionServiceInterface {

    List<Reserva> listarPorProgramar(Sesion sesion);

    RecursosDisponibles consultarRecursos(Sesion sesion, String idReserva, LocalDateTime fechaServicio);

    Reserva programar(Sesion sesion, ProgramacionServicio programacion);

    List<ItemAgenda> consultarAgenda(Sesion sesion, LocalDate desde, LocalDate hasta);
}
