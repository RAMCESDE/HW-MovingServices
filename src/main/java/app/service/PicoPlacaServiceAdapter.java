package app.service;

import app.domain.RestriccionPicoPlaca;
import app.domain.Sesion;
import app.domain.Vehiculo;
import app.domain.enums.Rol;
import app.domain.exceptions.DatoInvalidoException;
import app.domain.exceptions.RecursoNoEncontradoException;
import app.service.helpers.ControlAcceso;
import app.service.helpers.IdGenerator;
import app.service.inputports.PicoPlacaServiceInterface;
import app.service.inputports.dto.NuevaRestriccionPicoPlaca;
import app.service.outputports.RestriccionPicoPlacaRepositoryPort;
import app.service.outputports.VehiculoRepositoryPort;
import app.service.validators.DatosValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class PicoPlacaServiceAdapter implements PicoPlacaServiceInterface {

    private final RestriccionPicoPlacaRepositoryPort restricciones;
    private final VehiculoRepositoryPort vehiculos;

    public PicoPlacaServiceAdapter(RestriccionPicoPlacaRepositoryPort restricciones, VehiculoRepositoryPort vehiculos) {
        this.restricciones = restricciones;
        this.vehiculos = vehiculos;
    }

    @Override
    public RestriccionPicoPlaca crearRestriccion(Sesion sesion, NuevaRestriccionPicoPlaca datos) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        Set<Integer> digitos = DatosValidator.requerido(datos.digitos(), "dígitos");
        if (digitos.isEmpty() || digitos.stream().anyMatch(d -> d < 0 || d > 9)) {
            throw new DatoInvalidoException("Indique al menos un dígito de placa entre 0 y 9");
        }
        if (!datos.horaInicio().isBefore(datos.horaFin())) {
            throw new DatoInvalidoException("La hora de inicio debe ser anterior a la hora final");
        }
        if (datos.vigenteHasta().isBefore(datos.vigenteDesde())) {
            throw new DatoInvalidoException("La vigencia final no puede ser anterior a la inicial");
        }
        return restricciones.guardar(new RestriccionPicoPlaca(IdGenerator.nuevo("PYP"), datos.dia(), Set.copyOf(digitos),
                datos.horaInicio(), datos.horaFin(), datos.vigenteDesde(), datos.vigenteHasta()));
    }

    @Override
    public List<RestriccionPicoPlaca> listarRestricciones(Sesion sesion) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        return restricciones.listar();
    }

    @Override
    public void eliminarRestriccion(Sesion sesion, String id) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        if (!restricciones.eliminar(id)) {
            throw new RecursoNoEncontradoException("No existe la restricción " + id);
        }
    }

    @Override
    public boolean vehiculoRestringido(Sesion sesion, String placa, LocalDateTime inicio, LocalDateTime fin) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        String normalizada = DatosValidator.placa(placa);
        Vehiculo vehiculo = vehiculos.buscarPorPlaca(normalizada)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el vehículo " + normalizada));
        return restricciones.listar().stream().anyMatch(r -> r.restringe(vehiculo, inicio, fin));
    }
}
