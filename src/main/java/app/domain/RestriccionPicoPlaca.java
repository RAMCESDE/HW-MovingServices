package app.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/** Regla parametrizable: día de la semana, dígitos de placa, franja horaria y vigencia. */
public record RestriccionPicoPlaca(String id, DayOfWeek dia, Set<Integer> digitos, LocalTime horaInicio,
                                   LocalTime horaFin, LocalDate vigenteDesde, LocalDate vigenteHasta) {

    /** Indica si el vehículo queda restringido en algún momento del intervalo del servicio. */
    public boolean restringe(Vehiculo vehiculo, LocalDateTime inicio, LocalDateTime fin) {
        if (!digitos.contains(vehiculo.ultimoDigitoPlaca())) {
            return false;
        }
        for (LocalDate fecha = inicio.toLocalDate(); !fecha.isAfter(fin.toLocalDate()); fecha = fecha.plusDays(1)) {
            if (fecha.getDayOfWeek() == dia && !fecha.isBefore(vigenteDesde) && !fecha.isAfter(vigenteHasta)
                    && inicio.isBefore(fecha.atTime(horaFin)) && fin.isAfter(fecha.atTime(horaInicio))) {
                return true;
            }
        }
        return false;
    }
}
