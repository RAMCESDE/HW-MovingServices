package app.view;

import app.domain.AlertaDocumento;
import app.domain.Sesion;
import app.domain.Vehiculo;
import app.service.inputports.DocumentoVehiculoServiceInterface;
import app.view.helpers.EntradaConsola;

import java.util.List;

public class DocumentoVehiculoView {

    private final DocumentoVehiculoServiceInterface servicio;

    public DocumentoVehiculoView(DocumentoVehiculoServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void actualizar(Sesion sesion) {
        Vehiculo vehiculo = servicio.actualizarDocumentos(sesion, EntradaConsola.texto("Placa"),
                EntradaConsola.fecha("Vencimiento del SOAT"), EntradaConsola.fecha("Vencimiento de la tecnomecánica"));
        System.out.println("Documentos actualizados. Estado de " + vehiculo.getPlaca() + ": " + vehiculo.getEstado().getEtiqueta());
    }

    public void mostrarAlertas(Sesion sesion) {
        List<AlertaDocumento> alertas = servicio.revisarDocumentos(sesion);
        if (alertas.isEmpty()) {
            System.out.println("Documentos de la flota al día.");
        }
        for (AlertaDocumento a : alertas) {
            System.out.println("ALERTA " + a.placa() + " | " + a.documento().getEtiqueta() + " | " + a.estado().getEtiqueta()
                    + " | vence " + a.vencimiento() + " (" + a.diasRestantes() + " días)");
        }
    }
}
