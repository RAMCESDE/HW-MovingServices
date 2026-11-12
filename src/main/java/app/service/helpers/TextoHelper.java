package app.service.helpers;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formato de valores dentro de los mensajes que generan los servicios. */
public final class TextoHelper {

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private TextoHelper() {
    }

    public static String pesos(BigDecimal valor) {
        return "$" + NumberFormat.getIntegerInstance(Locale.of("es", "CO")).format(valor);
    }

    public static String fecha(LocalDateTime valor) {
        return valor.format(FECHA_HORA);
    }
}
