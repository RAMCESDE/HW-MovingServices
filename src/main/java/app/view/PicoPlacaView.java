package app.view;

import app.domain.RestriccionPicoPlaca;
import app.domain.Sesion;
import app.domain.exceptions.DatoInvalidoException;
import app.service.inputports.PicoPlacaServiceInterface;
import app.service.inputports.dto.NuevaRestriccionPicoPlaca;
import app.view.helpers.EntradaConsola;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class PicoPlacaView {

    private final PicoPlacaServiceInterface servicio;

    public PicoPlacaView(PicoPlacaServiceInterface servicio) {
        this.servicio = servicio;
    }

    public void crear(Sesion sesion) {
        DayOfWeek dia = EntradaConsola.opcion("Día", DayOfWeek.values(), PicoPlacaView::nombreDia);
        Set<Integer> digitos = leerDigitos(EntradaConsola.texto("Dígitos de placa separados por coma (ej. 1,7)"));
        RestriccionPicoPlaca creada = servicio.crearRestriccion(sesion, new NuevaRestriccionPicoPlaca(dia, digitos,
                EntradaConsola.hora("Hora de inicio"), EntradaConsola.hora("Hora final"),
                EntradaConsola.fecha("Vigente desde"), EntradaConsola.fecha("Vigente hasta")));
        System.out.println("Restricción " + creada.id() + " creada.");
    }

    public void listar(Sesion sesion) {
        List<RestriccionPicoPlaca> restricciones = servicio.listarRestricciones(sesion);
        if (restricciones.isEmpty()) {
            System.out.println("No hay restricciones parametrizadas.");
        }
        for (RestriccionPicoPlaca r : restricciones) {
            System.out.println(r.id() + " | " + nombreDia(r.dia()) + " | Dígitos " + r.digitos() + " | " + r.horaInicio()
                    + " a " + r.horaFin() + " | Vigencia " + r.vigenteDesde() + " a " + r.vigenteHasta());
        }
    }

    public void eliminar(Sesion sesion) {
        servicio.eliminarRestriccion(sesion, EntradaConsola.texto("Id de la restricción"));
        System.out.println("Restricción eliminada.");
    }

    public void consultarVehiculo(Sesion sesion) {
        String placa = EntradaConsola.texto("Placa");
        LocalDateTime inicio = EntradaConsola.fechaHora("Inicio");
        LocalDateTime fin = EntradaConsola.fechaHora("Fin");
        System.out.println(servicio.vehiculoRestringido(sesion, placa, inicio, fin)
                ? "El vehículo tiene restricción de Pico y Placa en ese horario."
                : "El vehículo puede circular en ese horario.");
    }

    private static Set<Integer> leerDigitos(String texto) {
        try {
            return Arrays.stream(texto.split(",")).map(String::trim).map(Integer::valueOf).collect(Collectors.toSet());
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("Los dígitos deben ser números separados por coma");
        }
    }

    private static String nombreDia(DayOfWeek dia) {
        return dia.getDisplayName(TextStyle.FULL, Locale.of("es", "CO"));
    }
}
