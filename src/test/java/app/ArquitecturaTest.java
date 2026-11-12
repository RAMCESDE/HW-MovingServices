package app;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Verifica la regla de dependencias de puertos y adaptadores leyendo los imports del código. */
class ArquitecturaTest {

    private static final Path RAIZ = Path.of("src/main/java/app");

    private List<String> violaciones(String capa, String... prohibidos) throws IOException {
        List<String> encontradas = new ArrayList<>();
        try (Stream<Path> archivos = Files.walk(RAIZ.resolve(capa))) {
            for (Path archivo : archivos.filter(p -> p.toString().endsWith(".java")).toList()) {
                for (String linea : Files.readAllLines(archivo)) {
                    for (String prohibido : prohibidos) {
                        if (linea.startsWith("import " + prohibido)) {
                            encontradas.add(archivo.getFileName() + " -> " + linea);
                        }
                    }
                }
            }
        }
        return encontradas;
    }

    @Test
    void elDominioNoDependeDeNingunaOtraCapa() throws IOException {
        assertEquals(List.of(), violaciones("domain", "app.service", "app.persistence", "app.configuration", "app.ui",
                "app.view", "java.sql", "java.util.Scanner"));
    }

    @Test
    void losServiciosSoloConocenPuertosYDominio() throws IOException {
        assertEquals(List.of(), violaciones("service", "app.persistence", "app.configuration", "app.ui", "app.view",
                "java.sql", "java.util.Scanner"));
    }

    @Test
    void laVistaNoConoceLaPersistenciaNiLasImplementaciones() throws IOException {
        assertEquals(List.of(), violaciones("view", "app.persistence", "app.configuration", "app.ui", "java.sql",
                "app.service.outputports", "app.service.CuentaServiceAdapter"));
        assertEquals(List.of(), violaciones("ui", "app.persistence", "app.configuration", "app.service", "java.sql"));
    }

    @Test
    void laPersistenciaNoConoceLaInterfazDeUsuario() throws IOException {
        assertEquals(List.of(), violaciones("persistence", "app.ui", "app.view", "app.configuration"));
    }
}
