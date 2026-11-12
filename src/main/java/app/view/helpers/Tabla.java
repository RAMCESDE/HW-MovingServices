package app.view.helpers;

import java.util.ArrayList;
import java.util.List;

/** Tabla de texto con columnas alineadas al dato más largo. */
public final class Tabla {

    private static final String SEPARADOR = " │ ";

    private final String[] encabezados;
    private final List<String[]> filas = new ArrayList<>();

    public Tabla(String... encabezados) {
        this.encabezados = encabezados;
    }

    /** Un valor null se muestra como "-". */
    public Tabla fila(Object... valores) {
        String[] fila = new String[encabezados.length];
        for (int i = 0; i < fila.length; i++) {
            Object valor = i < valores.length ? valores[i] : null;
            fila[i] = valor == null ? "-" : String.valueOf(valor);
        }
        filas.add(fila);
        return this;
    }

    /** Imprime la tabla o, si no tiene filas, el mensaje indicado. */
    public void imprimir(String mensajeVacio) {
        if (filas.isEmpty()) {
            Pantalla.info(mensajeVacio);
            return;
        }
        int[] anchos = new int[encabezados.length];
        for (int i = 0; i < anchos.length; i++) {
            anchos[i] = encabezados[i].length();
            for (String[] fila : filas) {
                anchos[i] = Math.max(anchos[i], fila[i].length());
            }
        }
        imprimirFila(encabezados, anchos);
        StringBuilder linea = new StringBuilder();
        for (int i = 0; i < anchos.length; i++) {
            linea.append(i == 0 ? "" : "─┼─").append("─".repeat(anchos[i]));
        }
        System.out.println(Pantalla.SANGRIA + linea);
        filas.forEach(fila -> imprimirFila(fila, anchos));
        Pantalla.info("(" + filas.size() + (filas.size() == 1 ? " registro)" : " registros)"));
    }

    private static void imprimirFila(String[] celdas, int[] anchos) {
        StringBuilder linea = new StringBuilder();
        for (int i = 0; i < celdas.length; i++) {
            linea.append(i == 0 ? "" : SEPARADOR).append(Pantalla.rellenar(celdas[i], anchos[i]));
        }
        System.out.println(Pantalla.SANGRIA + linea.toString().stripTrailing());
    }
}
