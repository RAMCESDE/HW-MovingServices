package app.view.helpers;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

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

    /** Elementos separados por coma, o "-" si no hay ninguno. */
    public static String lista(Collection<?> valores) {
        return valores == null || valores.isEmpty() ? "-"
                : valores.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }

    public static String decimal(double valor) {
        return String.format(Locale.of("es", "CO"), "%,.1f", valor);
    }
}
