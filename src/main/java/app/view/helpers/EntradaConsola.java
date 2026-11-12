package app.view.helpers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Function;

/** Lectura y reintento de datos por consola. Vive en la vista para que los servicios no dependan de Scanner. */
public final class EntradaConsola {

    private static final Scanner SC = new Scanner(System.in);
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private EntradaConsola() {
    }

    public static String texto(String mensaje) {
        while (true) {
            String valor = textoOpcional(mensaje);
            if (valor != null) {
                return valor;
            }
            Pantalla.error("Este dato es obligatorio");
        }
    }

    /** Devuelve null cuando el usuario deja el dato vacío. */
    public static String textoOpcional(String mensaje) {
        System.out.print(Pantalla.SANGRIA + "› " + mensaje + ": ");
        String valor = SC.nextLine().trim();
        return valor.isEmpty() ? null : valor;
    }

    public static int entero(String mensaje) {
        return leer(mensaje, Integer::valueOf, "Ingrese un número entero");
    }

    public static double decimal(String mensaje) {
        return leer(mensaje, v -> Double.valueOf(v.replace(',', '.')), "Ingrese un número");
    }

    public static boolean siNo(String mensaje) {
        while (true) {
            String valor = texto(mensaje + " (s/n)").toLowerCase(Locale.ROOT);
            if (valor.equals("s") || valor.equals("n")) {
                return valor.equals("s");
            }
            Pantalla.error("Responda s o n");
        }
    }

    public static LocalDate fecha(String mensaje) {
        return leer(mensaje + " (AAAA-MM-DD)", LocalDate::parse, "Use el formato AAAA-MM-DD");
    }

    public static LocalDate fechaOpcional(String mensaje) {
        while (true) {
            String valor = textoOpcional(mensaje + " (AAAA-MM-DD, vacío si no aplica)");
            if (valor == null) {
                return null;
            }
            try {
                return LocalDate.parse(valor);
            } catch (DateTimeParseException e) {
                Pantalla.error("Use el formato AAAA-MM-DD");
            }
        }
    }

    public static LocalDateTime fechaHora(String mensaje) {
        return leer(mensaje + " (AAAA-MM-DD HH:MM)", v -> LocalDateTime.parse(v, FECHA_HORA),
                "Use el formato AAAA-MM-DD HH:MM");
    }

    public static LocalTime hora(String mensaje) {
        return leer(mensaje + " (HH:MM)", LocalTime::parse, "Use el formato HH:MM");
    }

    public static BigDecimal dineroOpcional(String mensaje) {
        while (true) {
            String valor = textoOpcional(mensaje);
            if (valor == null) {
                return null;
            }
            try {
                return new BigDecimal(valor.replace(".", "").replace(",", ""));
            } catch (NumberFormatException e) {
                Pantalla.error("Ingrese un valor numérico");
            }
        }
    }

    public static <T extends Enum<T>> T opcion(String mensaje, T[] opciones, Function<T, String> etiqueta) {
        String[] textos = new String[opciones.length];
        for (int i = 0; i < opciones.length; i++) {
            textos[i] = etiqueta.apply(opciones[i]);
        }
        return opciones[opcion(mensaje, textos) - 1];
    }

    /** Muestra las opciones numeradas desde 1 y devuelve el número elegido. */
    public static int opcion(String mensaje, String... opciones) {
        System.out.println(Pantalla.SANGRIA + mensaje + ":");
        String formato = "%s    %" + String.valueOf(opciones.length).length() + "d) %s%n";
        for (int i = 0; i < opciones.length; i++) {
            System.out.printf(formato, Pantalla.SANGRIA, i + 1, opciones[i]);
        }
        while (true) {
            int elegida = entero("Seleccione");
            if (elegida >= 1 && elegida <= opciones.length) {
                return elegida;
            }
            Pantalla.error("Seleccione una opción válida");
        }
    }

    private static <T> T leer(String mensaje, Function<String, T> conversor, String error) {
        while (true) {
            try {
                return conversor.apply(texto(mensaje));
            } catch (NumberFormatException | DateTimeParseException e) {
                Pantalla.error(error);
            }
        }
    }
}
