package app.service.inputports;

import app.domain.Sesion;
import app.domain.SolicitudServicio;
import app.service.inputports.dto.SolicitudCotizacion;

import java.util.List;

public interface CotizacionServiceInterface {

    SolicitudServicio cotizar(Sesion sesion, SolicitudCotizacion datos);

    SolicitudServicio guardarBorrador(Sesion sesion, String idSolicitud);

    SolicitudServicio aceptar(Sesion sesion, String idSolicitud);

    SolicitudServicio rechazar(Sesion sesion, String idSolicitud);

    List<SolicitudServicio> listarMisCotizaciones(Sesion sesion);
}
