package app.service;

import app.domain.AlertaDocumento;
import app.domain.Sesion;
import app.domain.Vehiculo;
import app.domain.enums.EstadoDocumento;
import app.domain.enums.Rol;
import app.domain.enums.TipoDocumentoVehiculo;
import app.domain.exceptions.RecursoNoEncontradoException;
import app.service.helpers.ControlAcceso;
import app.service.inputports.DocumentoVehiculoServiceInterface;
import app.service.outputports.VehiculoRepositoryPort;
import app.service.validators.DatosValidator;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DocumentoVehiculoServiceAdapter implements DocumentoVehiculoServiceInterface {

    private final VehiculoRepositoryPort vehiculos;
    private final Clock reloj;

    public DocumentoVehiculoServiceAdapter(VehiculoRepositoryPort vehiculos, Clock reloj) {
        this.vehiculos = vehiculos;
        this.reloj = reloj;
    }

    @Override
    public Vehiculo actualizarDocumentos(Sesion sesion, String placa, LocalDate vencimientoSoat,
                                         LocalDate vencimientoTecnomecanica) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        String normalizada = DatosValidator.placa(placa);
        Vehiculo vehiculo = vehiculos.buscarPorPlaca(normalizada)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el vehículo " + normalizada));
        vehiculo.setVencimientoSoat(DatosValidator.requerido(vencimientoSoat, "vencimiento del SOAT"));
        vehiculo.setVencimientoTecnomecanica(DatosValidator.requerido(vencimientoTecnomecanica,
                "vencimiento de la tecnomecánica"));
        vehiculo.sincronizarEstadoConDocumentos(LocalDate.now(reloj));
        return vehiculos.guardar(vehiculo);
    }

    @Override
    public List<AlertaDocumento> revisarDocumentos(Sesion sesion) {
        ControlAcceso.exigirRol(sesion, Rol.ADMIN);
        LocalDate hoy = LocalDate.now(reloj);
        List<AlertaDocumento> alertas = new ArrayList<>();
        for (Vehiculo vehiculo : vehiculos.listar()) {
            var estadoAnterior = vehiculo.getEstado();
            vehiculo.sincronizarEstadoConDocumentos(hoy);
            if (vehiculo.getEstado() != estadoAnterior) {
                vehiculos.guardar(vehiculo);
            }
            for (TipoDocumentoVehiculo documento : TipoDocumentoVehiculo.values()) {
                EstadoDocumento estado = vehiculo.estadoDocumento(documento, hoy);
                if (estado != EstadoDocumento.VIGENTE) {
                    alertas.add(new AlertaDocumento(vehiculo.getPlaca(), documento, vehiculo.vencimientoDe(documento),
                            vehiculo.diasParaVencer(documento, hoy), estado));
                }
            }
        }
        alertas.sort(Comparator.comparingLong(AlertaDocumento::diasRestantes));
        return alertas;
    }
}
