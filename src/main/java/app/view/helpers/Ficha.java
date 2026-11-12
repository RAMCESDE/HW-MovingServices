package app.view.helpers;

import java.util.ArrayList;
import java.util.List;

/** Detalle de un registro como pares etiqueta/valor con las etiquetas alineadas. */
public final class Ficha {

    private final String titulo;
    private final List<String[]> campos = new ArrayList<>();

    public Ficha(String titulo) {
        this.titulo = titulo;
    }

    /** Un valor null se muestra como "-". */
    public Ficha campo(String etiqueta, Object valor) {
        campos.add(new String[]{etiqueta, valor == null ? "-" : String.valueOf(valor)});
        return this;
    }

    /** Línea divisoria, por ejemplo antes de un total. */
    public Ficha separador() {
        campos.add(null);
        return this;
    }

    public void imprimir() {
        Pantalla.subtitulo(titulo);
        int ancho = campos.stream().filter(c -> c != null).mapToInt(c -> c[0].length()).max().orElse(0);
        for (String[] campo : campos) {
            if (campo == null) {
                System.out.println(Pantalla.SANGRIA + "─".repeat(ancho + 20));
            } else {
                System.out.println(Pantalla.SANGRIA + Pantalla.rellenar(campo[0] + ":", ancho + 2) + campo[1]);
            }
        }
    }
}
