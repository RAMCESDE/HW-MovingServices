package app.service.inputports;

import app.domain.Sesion;
import app.service.inputports.dto.EstadoServicio;
import app.service.inputports.dto.ServicioAsignado;

import java.util.List;

public interface SeguimientoServiceInterface {

    EstadoServicio avanzarHito(Sesion sesion, String idReserva);

    EstadoServicio consultarEstado(Sesion sesion, String idReserva);

    List<ServicioAsignado> listarMisServicios(Sesion sesion);
}
