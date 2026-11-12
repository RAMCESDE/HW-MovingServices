package app.service.inputports.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ProgramacionServicio(String idReserva, LocalDateTime fechaServicio, String placaVehiculo,
                                   String idConductor, List<String> idsAuxiliares) {
}
