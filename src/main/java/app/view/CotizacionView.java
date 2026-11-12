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
import app.view.helpers.Formato;

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
        TipoServicio tipo = EntradaConsola.opcion("Tipo de servicio", TipoServicio.values(), TipoServicio::getEtiqueta);
        LocalDate fecha = EntradaConsola.fecha("Fecha deseada");
        System.out.println("--- Origen ---");
        Ubicacion origen = leerUbicacion();
        System.out.println("--- Destino ---");
        Ubicacion destino = leerUbicacion();
        Map<Enser, Integer> enseres = new EnumMap<>(Enser.class);
        double volumen = 0;
        if (EntradaConsola.siNo("¿Desea ingresar la lista de enseres? (n = indicar el volumen en m³)")) {
            for (Enser enser : Enser.values()) {
                int cantidad = EntradaConsola.entero(enser.getEtiqueta() + " (cantidad, 0 si no aplica)");
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
        if (cotizaciones.isEmpty()) {
            System.out.println("No tiene cotizaciones.");
        }
        cotizaciones.forEach(this::mostrar);
    }

    public void retomar(Sesion sesion) {
        decidir(sesion, EntradaConsola.texto("Id de la cotización"));
    }

    private void decidir(Sesion sesion, String id) {
        int opcion = EntradaConsola.entero("1. Aceptar  2. Rechazar  3. Guardar borrador  4. Decidir después");
        SolicitudServicio resultado = switch (opcion) {
            case 1 -> servicio.aceptar(sesion, id);
            case 2 -> servicio.rechazar(sesion, id);
            case 3 -> servicio.guardarBorrador(sesion, id);
            default -> null;
        };
        if (resultado != null) {
            System.out.println("Cotización " + id + ": " + resultado.getEstado().getEtiqueta());
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
        System.out.println("\nCotización " + s.getIdSolicitud() + " | " + s.getEstado().getEtiqueta() + " | "
                + s.getTipoServicio().getEtiqueta() + " | " + s.getFecha()
                + "\n" + s.getOrigen().municipio().getEtiqueta() + " → " + s.getDestino().municipio().getEtiqueta()
                + " | " + c.distanciaKm() + " km aprox. | " + String.format("%.2f", s.getVolumenM3()) + " m³"
                + " | Vehículo sugerido: " + c.vehiculoSugerido().getEtiqueta()
                + "\n  Tarifa base:        " + Formato.pesos(c.tarifaBase())
                + "\n  Recargo distancia:  " + Formato.pesos(c.recargoDistancia())
                + "\n  Pisos/complejidad:  " + Formato.pesos(c.recargoPisos())
                + "\n  Personal adicional: " + Formato.pesos(c.recargoPersonal())
                + "\n  TOTAL ESTIMADO:     " + Formato.pesos(c.total()));
    }
}
