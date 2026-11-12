package app.view;

import app.domain.AlertaDocumento;
import app.domain.Sesion;
import app.domain.Vehiculo;
import app.service.inputports.DocumentoVehiculoServiceInterface;
import app.view.helpers.EntradaConsola;
import app.view.helpers.Pantalla;
import app.view.helpers.Tabla;

import java.util.List;

public class DocumentoVehiculoView {

    private final DocumentoVehiculoServiceInterface servicio;

    public DocumentoVehiculoView(DocumentoVehiculoServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void actualizar(Sesion sesion) {
        Pantalla.subtitulo("Actualizar documentos de vehículo");
        Vehiculo vehiculo = servicio.actualizarDocumentos(sesion, EntradaConsola.texto("Placa"),
                EntradaConsola.fecha("Vencimiento del SOAT"), EntradaConsola.fecha("Vencimiento de la tecnomecánica"));
        Pantalla.exito("Documentos actualizados. Estado de " + vehiculo.getPlaca() + ": "
                + vehiculo.getEstado().getEtiqueta());
    }

    public void mostrarAlertas(Sesion sesion) {
        List<AlertaDocumento> alertas = servicio.revisarDocumentos(sesion);
        Pantalla.subtitulo("Alertas de documentos");
        if (alertas.isEmpty()) {
            Pantalla.exito("Documentos de la flota al día.");
            return;
        }
        Pantalla.aviso(alertas.size() + " documento(s) requieren atención:");
        Tabla tabla = new Tabla("Placa", "Documento", "Estado", "Vence", "Días restantes");
        for (AlertaDocumento a : alertas) {
            tabla.fila(a.placa(), a.documento().getEtiqueta(), a.estado().getEtiqueta(), a.vencimiento(), a.diasRestantes());
        }
        tabla.imprimir("");
    }
}
