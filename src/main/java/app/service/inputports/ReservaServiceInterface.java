package app.service.inputports;

import app.domain.Reserva;
import app.domain.Sesion;
import app.domain.enums.MetodoPago;
import app.service.inputports.dto.ResultadoReserva;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservaServiceInterface {

    ResultadoReserva reservar(Sesion sesion, String idSolicitud, LocalDateTime fechaServicio, MetodoPago metodo,
                              BigDecimal anticipo, String tokenPago);

    /** El administrador registra que recibió un anticipo en efectivo. */
    ResultadoReserva registrarAnticipoEfectivo(Sesion sesion, String idReserva);

    List<Reserva> listarMisReservas(Sesion sesion);

    List<Reserva> listarAnticiposPendientes(Sesion sesion);
}
