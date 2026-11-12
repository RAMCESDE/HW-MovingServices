package app.service.inputports.dto;

import app.domain.enums.CargoEmpleado;

import java.time.LocalDate;

public record RegistroEmpleado(String nombre, String cedula, String telefono, String email, String passwordTemporal,
                               CargoEmpleado cargo, String turno, Integer edad, String sexo, String estadoCivil,
                               String eps, String tipoLicencia, LocalDate vencimientoLicencia,
                               String tallaDotacion) {
}
