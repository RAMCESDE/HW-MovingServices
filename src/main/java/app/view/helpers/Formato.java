package app.view.helpers;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formato {

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Formato() {
    }

    public static String pesos(BigDecimal valor) {
        return "$" + NumberFormat.getIntegerInstance(Locale.of("es", "CO")).format(valor);
    }

    public static String fechaHora(LocalDateTime valor) {
        return valor == null ? "-" : valor.format(FECHA_HORA);
    }

    public static String texto(String valor) {
        return valor == null ? "-" : valor;
    }
}
