package app.view.helpers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Menú de consola agrupado por secciones. Solo devuelve números de opción que existen en el menú. */
public final class Menu {

    private final String titulo;
    private final String subtitulo;
    private final List<String[]> lineas = new ArrayList<>();
    private final Set<Integer> validas = new LinkedHashSet<>();

    public Menu(String titulo, String subtitulo) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
    }

    public Menu(String titulo) {
        this(titulo, null);
    }

    public Menu seccion(String nombre) {
        lineas.add(new String[]{null, nombre});
        return this;
    }

    public Menu opcion(int numero, String texto) {
        lineas.add(new String[]{String.valueOf(numero), texto});
        validas.add(numero);
        return this;
    }

    /** Opción 0, separada del resto: salir o cerrar sesión. */
    public Menu salida(String texto) {
        lineas.add(new String[]{"", null});
        return opcion(0, texto);
    }

    public int leer() {
        imprimir();
        while (true) {
            int elegida = EntradaConsola.entero("Seleccione una opción");
            if (validas.contains(elegida)) {
                return elegida;
            }
            Pantalla.error("Opción no válida");
        }
    }

    private void imprimir() {
        Pantalla.titulo(titulo, subtitulo);
        int anchoNumero = validas.stream().mapToInt(n -> String.valueOf(n).length()).max().orElse(1);
        for (String[] linea : lineas) {
            if (linea[0] == null) {
                System.out.println();
                System.out.println(Pantalla.SANGRIA + linea[1].toUpperCase());
            } else if (linea[1] == null) {
                System.out.println(Pantalla.SANGRIA + "─".repeat(Pantalla.ANCHO + 2));
            } else {
                System.out.printf("%s  %" + anchoNumero + "s) %s%n", Pantalla.SANGRIA, linea[0], linea[1]);
            }
        }
        System.out.println();
    }
}
