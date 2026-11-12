package app.service.inputports.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record NuevaRestriccionPicoPlaca(DayOfWeek dia, Set<Integer> digitos, LocalTime horaInicio, LocalTime horaFin,
                                        LocalDate vigenteDesde, LocalDate vigenteHasta) {
}
