package app.persistence.mappers;

import app.domain.DesgloseCotizacion;
import app.domain.SolicitudServicio;
import app.domain.Ubicacion;
import app.domain.enums.Enser;
import app.domain.enums.EstadoSolicitud;
import app.domain.enums.Municipio;
import app.domain.enums.TipoCarga;
import app.domain.enums.TipoServicio;
import app.domain.enums.TipoVehiculo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SolicitudServicioMapper implements RowMapper<SolicitudServicio> {

    @Override
    public SolicitudServicio mapRow(ResultSet rs) throws SQLException {
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setIdSolicitud(rs.getString("id_solicitud"));
        solicitud.setIdCliente(rs.getString("id_cliente"));
        solicitud.setTipoServicio(TipoServicio.valueOf(rs.getString("tipo_servicio")));
        solicitud.setEstado(EstadoSolicitud.valueOf(rs.getString("estado")));
        solicitud.setFecha(rs.getObject("fecha", LocalDate.class));
        solicitud.setOrigen(ubicacion(rs, "origen"));
        solicitud.setDestino(ubicacion(rs, "destino"));
        solicitud.setVolumenM3(rs.getDouble("volumen_m3"));
        solicitud.setEnseres(leerEnseres(rs.getString("enseres")));
        solicitud.setTipoCarga(TipoCarga.valueOf(rs.getString("tipo_carga")));
        solicitud.setAyudantes(rs.getInt("ayudantes"));
        solicitud.setCotizacion(new DesgloseCotizacion(rs.getBigDecimal("tarifa_base"),
                rs.getBigDecimal("recargo_distancia"), rs.getBigDecimal("recargo_pisos"),
                rs.getBigDecimal("recargo_personal"), rs.getBigDecimal("total"),
                TipoVehiculo.valueOf(rs.getString("vehiculo_sugerido")), rs.getDouble("distancia_km"),
                rs.getInt("duracion_minutos")));
        solicitud.setFechaCreacion(rs.getObject("fecha_creacion", LocalDateTime.class));
        return solicitud;
    }

    private Ubicacion ubicacion(ResultSet rs, String prefijo) throws SQLException {
        return new Ubicacion(Municipio.valueOf(rs.getString(prefijo + "_municipio")),
                rs.getString(prefijo + "_direccion"), rs.getInt(prefijo + "_piso"), rs.getBoolean(prefijo + "_ascensor"));
    }

    public static String escribirEnseres(Map<Enser, Integer> enseres) {
        return enseres.entrySet().stream().map(e -> e.getKey().name() + ":" + e.getValue())
                .collect(Collectors.joining(";"));
    }

    private static Map<Enser, Integer> leerEnseres(String texto) {
        Map<Enser, Integer> enseres = new EnumMap<>(Enser.class);
        if (texto != null && !texto.isBlank()) {
            for (String item : texto.split(";")) {
                String[] partes = item.split(":");
                enseres.put(Enser.valueOf(partes[0]), Integer.valueOf(partes[1]));
            }
        }
        return enseres;
    }
}
