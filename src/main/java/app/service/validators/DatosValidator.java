package app.service.validators;

import app.domain.exceptions.DatoInvalidoException;

import java.util.Locale;
import java.util.regex.Pattern;

/** Reglas de formato de los datos de negocio; no depende de la consola ni de la base de datos. */
public final class DatosValidator {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s]).{8,}$");
    private static final Pattern TELEFONO = Pattern.compile("^(3\\d{9}|60\\d{8})$");
    private static final Pattern PLACA = Pattern.compile("^([A-Z]{3}\\d{3}|[A-Z]{3}\\d{2}[A-Z]|\\d{3}[A-Z]{3})$");

    private DatosValidator() {
    }

    public static String email(String valor) {
        String email = requerido(valor, "correo").toLowerCase(Locale.ROOT);
        if (!EMAIL.matcher(email).matches()) {
            throw new DatoInvalidoException("El correo electrónico no es válido");
        }
        return email;
    }

    public static String password(String valor) {
        if (valor == null || !PASSWORD.matcher(valor).matches()) {
            throw new DatoInvalidoException("La contraseña debe tener mínimo 8 caracteres, una mayúscula, "
                    + "un número y un carácter especial");
        }
        return valor;
    }

    public static String telefono(String valor) {
        String telefono = requerido(valor, "teléfono").replaceAll("[\\s-]", "");
        if (!TELEFONO.matcher(telefono).matches()) {
            throw new DatoInvalidoException("El teléfono debe ser un celular de 10 dígitos o un fijo con indicativo 60");
        }
        return telefono;
    }

    public static String placa(String valor) {
        String placa = requerido(valor, "placa").replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT);
        if (!PLACA.matcher(placa).matches()) {
            throw new DatoInvalidoException("La placa " + placa + " no tiene un formato válido");
        }
        return placa;
    }

    public static String requerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DatoInvalidoException("El campo " + campo + " es obligatorio");
        }
        return valor.trim();
    }

    public static <T> T requerido(T valor, String campo) {
        if (valor == null) {
            throw new DatoInvalidoException("El campo " + campo + " es obligatorio");
        }
        return valor;
    }

    public static double positivo(double valor, String campo) {
        if (valor <= 0) {
            throw new DatoInvalidoException("El campo " + campo + " debe ser mayor que cero");
        }
        return valor;
    }
}
