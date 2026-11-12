package app.service.helpers;

import app.domain.DesgloseCotizacion;
import app.domain.SolicitudServicio;
import app.domain.TarifaParametros;
import app.domain.enums.TipoServicio;
import app.domain.enums.TipoVehiculo;
import app.domain.exceptions.ReglaNegocioException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Precio = tarifa base del vehículo sugerido + recargo por distancia
 * + recargo por pisos sin ascensor + personal adicional.
 */
public class EstrategiaPrecioPorVehiculo implements EstrategiaPrecio {

    private static final int MINUTOS_BASE = 120;
    private static final int MINUTOS_POR_KM = 3;
    private static final int MINUTOS_POR_PISO = 15;
    private static final int MINUTOS_POR_M3 = 5;

    private final TarifaParametros tarifas;

    public EstrategiaPrecioPorVehiculo(TarifaParametros tarifas) {
        this.tarifas = tarifas;
    }

    @Override
    public DesgloseCotizacion calcular(SolicitudServicio solicitud, double distanciaKm) {
        TipoVehiculo vehiculo = sugerirVehiculo(solicitud.getVolumenM3());
        int pisos = solicitud.getOrigen().pisosPorEscalera() + solicitud.getDestino().pisosPorEscalera();

        BigDecimal base = tarifas.tarifaBase().get(vehiculo);
        if (solicitud.getTipoServicio() == TipoServicio.ACARREO) {
            base = base.multiply(BigDecimal.valueOf(tarifas.factorAcarreo()));
        }
        BigDecimal distancia = tarifas.valorKm().get(vehiculo).multiply(BigDecimal.valueOf(distanciaKm));
        BigDecimal recargoPisos = tarifas.valorPiso().multiply(BigDecimal.valueOf(pisos))
                .multiply(BigDecimal.valueOf(solicitud.getTipoCarga().getFactorPisos()));
        BigDecimal personal = tarifas.valorAyudante().multiply(BigDecimal.valueOf(solicitud.getAyudantes()));

        base = pesos(base);
        distancia = pesos(distancia);
        recargoPisos = pesos(recargoPisos);
        personal = pesos(personal);
        BigDecimal total = base.add(distancia).add(recargoPisos).add(personal);

        int duracion = (int) Math.ceil(MINUTOS_BASE + distanciaKm * MINUTOS_POR_KM + pisos * MINUTOS_POR_PISO
                + solicitud.getVolumenM3() * MINUTOS_POR_M3);
        return new DesgloseCotizacion(base, distancia, recargoPisos, personal, total, vehiculo, distanciaKm, duracion);
    }

    private TipoVehiculo sugerirVehiculo(double volumenM3) {
        for (TipoVehiculo tipo : TipoVehiculo.values()) {
            if (volumenM3 <= tarifas.capacidadM3().get(tipo)) {
                return tipo;
            }
        }
        throw new ReglaNegocioException("El volumen supera la capacidad de un vehículo; requiere cotización manual");
    }

    private BigDecimal pesos(BigDecimal valor) {
        return valor.setScale(0, RoundingMode.HALF_UP);
    }
}
