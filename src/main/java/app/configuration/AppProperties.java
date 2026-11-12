package app.configuration;

import app.domain.exceptions.PersistenciaException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/** Lee application.properties; una variable de entorno HW_* tiene prioridad sobre el archivo. */
public class AppProperties {

    private final Properties propiedades = new Properties();

    public AppProperties() {
        try (InputStream entrada = AppProperties.class.getResourceAsStream("/application.properties")) {
            if (entrada == null) {
                throw new PersistenciaException("No se encontró application.properties");
            }
            propiedades.load(entrada);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer application.properties: " + e.getMessage());
        }
    }

    public String get(String clave) {
        String variable = "HW_" + clave.replace('.', '_').toUpperCase(Locale.ROOT);
        String valor = System.getenv(variable);
        return valor != null ? valor : propiedades.getProperty(clave, "");
    }
}
