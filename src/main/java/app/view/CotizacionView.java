package app.view;

import app.domain.DesgloseCotizacion;
import app.domain.Sesion;
import app.domain.SolicitudServicio;
import app.domain.Ubicacion;
import app.domain.enums.Enser;
import app.domain.enums.Municipio;
import app.domain.enums.TipoCarga;
import app.domain.enums.TipoServicio;
import app.service.inputports.CotizacionServiceInterface;
import app.service.inputports.dto.SolicitudCotizacion;
import app.view.helpers.EntradaConsola;
import app.view.helpers.Ficha;
import app.view.helpers.Formato;
import app.view.helpers.Pantalla;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CotizacionView {

    private final CotizacionServiceInterface servicio;

    public CotizacionView(CotizacionServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void solicitar(Sesion sesion) {
        Pantalla.subtitulo("Nueva cotización");
        TipoServicio tipo = EntradaConsola.opcion("Tipo de servicio", TipoServicio.values(), TipoServicio::getEtiqueta);
        LocalDate fecha = EntradaConsola.fecha("Fecha deseada");
        Pantalla.subtitulo("Origen");
        Ubicacion origen = leerUbicacion();
        Pantalla.subtitulo("Destino");
        Ubicacion destino = leerUbicacion();
        Map<Enser, Integer> enseres = new EnumMap<>(Enser.class);
        double volumen = 0;
        Pantalla.subtitulo("Carga");
        if (EntradaConsola.siNo("¿Desea ingresar la lista de enseres? (n = indicar el volumen en m³)")) {
            Pantalla.info("Indique la cantidad de cada enser (0 si no aplica).");
            for (Enser enser : Enser.values()) {
                int cantidad = EntradaConsola.entero(enser.getEtiqueta());
                if (cantidad != 0) {
                    enseres.put(enser, cantidad);
                }
            }
        } else {
            volumen = EntradaConsola.decimal("Volumen (m³)");
        }
        TipoCarga carga = EntradaConsola.opcion("Tipo de carga", TipoCarga.values(), TipoCarga::getEtiqueta);
        int ayudantes = EntradaConsola.entero("Ayudantes requeridos");

        SolicitudServicio solicitud = servicio.cotizar(sesion, new SolicitudCotizacion(tipo, fecha, origen, destino,
                volumen, enseres, carga, ayudantes));
        mostrar(solicitud);
        decidir(sesion, solicitud.getIdSolicitud());
    }

    public void listar(Sesion sesion) {
        List<SolicitudServicio> cotizaciones = servicio.listarMisCotizaciones(sesion);
        Pantalla.subtitulo("Mis cotizaciones");
        if (cotizaciones.isEmpty()) {
            Pantalla.info("No tiene cotizaciones.");
        }
        cotizaciones.forEach(this::mostrar);
    }

    public void retomar(Sesion sesion) {
        Pantalla.subtitulo("Decidir cotización");
        decidir(sesion, EntradaConsola.texto("Id de la cotización"));
    }

    private void decidir(Sesion sesion, String id) {
        System.out.println();
        int opcion = EntradaConsola.opcion("¿Qué desea hacer con la cotización?", "Aceptar", "Rechazar",
                "Guardar borrador", "Decidir después");
        SolicitudServicio resultado = switch (opcion) {
            case 1 -> servicio.aceptar(sesion, id);
            case 2 -> servicio.rechazar(sesion, id);
            case 3 -> servicio.guardarBorrador(sesion, id);
            default -> null;
        };
        if (resultado != null) {
            Pantalla.exito("Cotización " + id + ": " + resultado.getEstado().getEtiqueta());
        }
    }

    private Ubicacion leerUbicacion() {
        Municipio municipio = EntradaConsola.opcion("Municipio", Municipio.values(), Municipio::getEtiqueta);
        String direccion = EntradaConsola.texto("Dirección");
        int piso = EntradaConsola.entero("Piso");
        boolean ascensor = EntradaConsola.siNo("¿Tiene ascensor?");
        return new Ubicacion(municipio, direccion, piso, ascensor);
    }

    private void mostrar(SolicitudServicio s) {
        DesgloseCotizacion c = s.getCotizacion();
        new Ficha("Cotización " + s.getIdSolicitud())
                .campo("Estado", s.getEstado().getEtiqueta())
                .campo("Servicio", s.getTipoServicio().getEtiqueta())
                .campo("Fecha", s.getFecha())
                .campo("Ruta", s.getOrigen().municipio().getEtiqueta() + " → " + s.getDestino().municipio().getEtiqueta())
                .campo("Distancia", c.distanciaKm() + " km aprox.")
                .campo("Volumen", String.format("%.2f", s.getVolumenM3()) + " m³")
                .campo("Vehículo sugerido", c.vehiculoSugerido().getEtiqueta())
                .separador()
                .campo("Tarifa base", Formato.pesos(c.tarifaBase()))
                .campo("Recargo distancia", Formato.pesos(c.recargoDistancia()))
                .campo("Pisos/complejidad", Formato.pesos(c.recargoPisos()))
                .campo("Personal adicional", Formato.pesos(c.recargoPersonal()))
                .separador()
                .campo("TOTAL ESTIMADO", Formato.pesos(c.total()))
                .imprimir();
    }
}
