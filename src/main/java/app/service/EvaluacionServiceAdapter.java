package app.service;

import app.domain.Empleado;
import app.domain.Evaluacion;
import app.domain.Reserva;
import app.domain.Sesion;
import app.domain.enums.EstadoReserva;
import app.domain.enums.Rol;
import app.domain.exceptions.RecursoNoEncontradoException;
import app.domain.exceptions.ReglaNegocioException;
import app.service.helpers.ControlAcceso;
import app.service.helpers.IdGenerator;
import app.service.inputports.EvaluacionServiceInterface;
import app.service.inputports.dto.MetricasCalidad;
import app.service.outputports.EmpleadoRepositoryPort;
import app.service.outputports.EvaluacionRepositoryPort;
import app.service.outputports.ReservaRepositoryPort;
import app.service.outputports.TransaccionPort;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EvaluacionServiceAdapter implements EvaluacionServiceInterface {

    private final EvaluacionRepositoryPort evaluaciones;
    private final ReservaRepositoryPort reservas;
    private final EmpleadoRepositoryPort empleados;
    private final TransaccionPort transaccion;
    private final Clock reloj;

    public EvaluacionServiceAdapter(EvaluacionRepositoryPort evaluaciones, ReservaRepositoryPort reservas,
                                    EmpleadoRepositoryPort empleados, TransaccionPort transaccion, Clock reloj) {
        this.evaluaciones = evaluaciones;
        this.reservas = reservas;
        this.empleados = empleados;
        this.transaccion = transaccion;
        this.reloj = reloj;
    }

    @Override
    public Evaluacion calificar(Sesion sesion, String idReserva, int puntuacionServicio, int puntuacionPersonal,
                                String comentario) {
        Reserva reserva = reservas.buscarPorId(idReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la reserva " + idReserva));
        ControlAcceso.exigirPropietario(sesion, reserva.getIdCliente());
        if (reserva.getEstado() != EstadoReserva.FINALIZADO) {
            throw new ReglaNegocioException("Solo se puede calificar un servicio finalizado y pagado");
        }
        if (evaluaciones.buscarPorReserva(idReserva).isPresent()) {
            throw new ReglaNegocioException("Este servicio ya fue calificado");
        }
        Evaluacion evaluacion = new Evaluacion(IdGenerator.nuevo("EVA"), idReserva, puntuacionServicio,
                puntuacionPersonal, comentario, LocalDateTime.now(reloj));
        List<String> tripulacion = new ArrayList<>(reserva.getIdsAuxiliares());
        tripulacion.add(reserva.getIdConductor());
        return transaccion.ejecutar(() -> {
            evaluaciones.guardar(evaluacion);
            for (String idEmpleado : tripulacion) {
                empleados.buscarPorId(idEmpleado).ifPresent(empleado -> {
                    empleado.registrarCalificacion(puntuacionPersonal);
                    empleados.guardar(empleado);
                });
            }
            return evaluacion;
        });
    }

    @Override
    public MetricasCalidad consultarMetricas(Sesion sesion) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        List<Evaluacion> todas = evaluaciones.listar();
        double servicio = todas.stream().mapToInt(Evaluacion::puntuacionServicio).average().orElse(0);
        double personal = todas.stream().mapToInt(Evaluacion::puntuacionPersonal).average().orElse(0);
        List<MetricasCalidad.CalificacionEmpleado> porEmpleado = empleados.listar().stream()
                .filter(e -> e.getTotalCalificaciones() > 0)
                .sorted(Comparator.comparingDouble(Empleado::getCalificacionPromedio).reversed())
                .map(e -> new MetricasCalidad.CalificacionEmpleado(e.getIdEmpleado(), e.getNombre(),
                        e.getCalificacionPromedio(), e.getTotalCalificaciones()))
                .toList();
        return new MetricasCalidad(todas.size(), servicio, personal, porEmpleado);
    }
}
