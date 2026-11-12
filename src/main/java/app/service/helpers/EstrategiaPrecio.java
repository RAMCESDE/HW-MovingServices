package app.service.helpers;

import app.domain.DesgloseCotizacion;
import app.domain.SolicitudServicio;

/** Permite agregar otros algoritmos de precios sin modificar el servicio de cotización (OCP). */
public interface EstrategiaPrecio {

    DesgloseCotizacion calcular(SolicitudServicio solicitud, double distanciaKm);
}
