package app.service;

import app.domain.Sesion;
import app.domain.SolicitudServicio;
import app.domain.Ubicacion;
import app.domain.enums.Enser;
import app.domain.enums.EstadoSolicitud;
import app.domain.enums.Rol;
import app.domain.enums.TipoServicio;
import app.domain.exceptions.DatoInvalidoException;
import app.domain.exceptions.RecursoNoEncontradoException;
import app.service.helpers.ControlAcceso;
import app.service.helpers.EstrategiaPrecio;
import app.service.helpers.IdGenerator;
import app.service.inputports.CotizacionServiceInterface;
import app.service.inputports.dto.SolicitudCotizacion;
import app.service.outputports.DistanciaPort;
import app.service.outputports.SolicitudServicioRepositoryPort;
import app.service.validators.DatosValidator;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class CotizacionServiceAdapter implements CotizacionServiceInterface {

    private final SolicitudServicioRepositoryPort solicitudes;
    private final DistanciaPort distancia;
    private final EstrategiaPrecio estrategiaPrecio;
    private final Clock reloj;

    public CotizacionServiceAdapter(SolicitudServicioRepositoryPort solicitudes, DistanciaPort distancia,
                                    EstrategiaPrecio estrategiaPrecio, Clock reloj) {
        this.solicitudes = solicitudes;
        this.distancia = distancia;
        this.estrategiaPrecio = estrategiaPrecio;
        this.reloj = reloj;
    }

    @Override
    public SolicitudServicio cotizar(Sesion sesion, SolicitudCotizacion datos) {
        ControlAcceso.exigirRol(sesion, Rol.CLIENTE);
        TipoServicio tipo = DatosValidator.requerido(datos.tipoServicio(), "tipo de servicio");
        LocalDate fecha = DatosValidator.requerido(datos.fecha(), "fecha");
        if (fecha.isBefore(LocalDate.now(reloj))) {
            throw new DatoInvalidoException("La fecha del servicio no puede estar en el pasado");
        }
        validarUbicacion(datos.origen(), "origen");
        validarUbicacion(datos.destino(), "destino");
        if (datos.ayudantes() < 0) {
            throw new DatoInvalidoException("La cantidad de ayudantes no puede ser negativa");
        }

        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setIdSolicitud(IdGenerator.nuevo("SOL"));
        solicitud.setIdCliente(sesion.idPerfil());
        solicitud.setTipoServicio(tipo);
        solicitud.setEstado(EstadoSolicitud.COTIZADA);
        solicitud.setFecha(fecha);
        solicitud.setOrigen(datos.origen());
        solicitud.setDestino(datos.destino());
        Map<Enser, Integer> enseres = datos.enseres() == null ? Map.of() : datos.enseres();
        solicitud.setEnseres(enseres);
        solicitud.setVolumenM3(calcularVolumen(datos.volumenM3(), enseres));
        solicitud.setTipoCarga(DatosValidator.requerido(datos.tipoCarga(), "tipo de carga"));
        solicitud.setAyudantes(datos.ayudantes());
        solicitud.setFechaCreacion(LocalDateTime.now(reloj));
        solicitud.setCotizacion(estrategiaPrecio.calcular(solicitud, distancia.calcularKm(datos.origen(), datos.destino())));
        return solicitudes.guardar(solicitud);
    }

    @Override
    public SolicitudServicio guardarBorrador(Sesion sesion, String idSolicitud) {
        SolicitudServicio solicitud = buscarPropia(sesion, idSolicitud);
        solicitud.guardarBorrador();
        return solicitudes.guardar(solicitud);
    }

    @Override
    public SolicitudServicio aceptar(Sesion sesion, String idSolicitud) {
        SolicitudServicio solicitud = buscarPropia(sesion, idSolicitud);
        solicitud.aceptar();
        return solicitudes.guardar(solicitud);
    }

    @Override
    public SolicitudServicio rechazar(Sesion sesion, String idSolicitud) {
        SolicitudServicio solicitud = buscarPropia(sesion, idSolicitud);
        solicitud.rechazar();
        return solicitudes.guardar(solicitud);
    }

    @Override
    public List<SolicitudServicio> listarMisCotizaciones(Sesion sesion) {
        ControlAcceso.exigirRol(sesion, Rol.CLIENTE);
        return solicitudes.listarPorCliente(sesion.idPerfil());
    }

    private SolicitudServicio buscarPropia(Sesion sesion, String idSolicitud) {
        ControlAcceso.exigirRol(sesion, Rol.CLIENTE);
        SolicitudServicio solicitud = solicitudes.buscarPorId(idSolicitud)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la cotización " + idSolicitud));
        ControlAcceso.exigirPropietario(sesion, solicitud.getIdCliente());
        return solicitud;
    }

    private void validarUbicacion(Ubicacion ubicacion, String campo) {
        DatosValidator.requerido(ubicacion, campo);
        DatosValidator.requerido(ubicacion.municipio(), "municipio de " + campo);
        DatosValidator.requerido(ubicacion.direccion(), "dirección de " + campo);
        if (ubicacion.piso() < 1) {
            throw new DatoInvalidoException("El piso de " + campo + " debe ser 1 o mayor");
        }
    }

    private double calcularVolumen(double volumenIndicado, Map<Enser, Integer> enseres) {
        if (enseres.isEmpty()) {
            return DatosValidator.positivo(volumenIndicado, "volumen");
        }
        double volumen = 0;
        for (Map.Entry<Enser, Integer> item : enseres.entrySet()) {
            if (item.getValue() <= 0) {
                throw new DatoInvalidoException("La cantidad de " + item.getKey().getEtiqueta() + " debe ser mayor que cero");
            }
            volumen += item.getKey().getVolumenM3() * item.getValue();
        }
        return volumen;
    }
}
