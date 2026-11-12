package app.view.helpers;

/** Presentación uniforme de la consola: títulos, secciones, recuadros y mensajes de resultado. */
public final class Pantalla {

    static final int ANCHO = 60;
    static final String SANGRIA = "  ";

    private Pantalla() {
    }

    /** Título principal enmarcado, con un subtítulo opcional debajo del nombre. */
    public static void titulo(String texto, String subtitulo) {
        String borde = "═".repeat(ANCHO);
        System.out.println();
        System.out.println(SANGRIA + "╔" + borde + "╗");
        System.out.println(SANGRIA + "║" + centrar(texto, ANCHO) + "║");
        if (subtitulo != null) {
            System.out.println(SANGRIA + "║" + centrar(subtitulo, ANCHO) + "║");
        }
        System.out.println(SANGRIA + "╚" + borde + "╝");
    }

    public static void titulo(String texto) {
        titulo(texto, null);
    }

    /** Encabezado de una sección dentro de una pantalla: ── Texto ──────── */
    public static void subtitulo(String texto) {
        String inicio = "── " + texto + " ";
        System.out.println();
        System.out.println(SANGRIA + inicio + "─".repeat(Math.max(3, ANCHO + 2 - inicio.length())));
    }

    /** Enmarca un texto de varias líneas, como un comprobante. */
    public static void recuadro(String texto) {
        String[] lineas = texto.split("\n");
        int ancho = ANCHO - 2;
        for (String linea : lineas) {
            ancho = Math.max(ancho, linea.length());
        }
        System.out.println();
        System.out.println(SANGRIA + "┌" + "─".repeat(ancho + 2) + "┐");
        for (String linea : lineas) {
            System.out.println(SANGRIA + "│ " + rellenar(linea, ancho) + " │");
        }
        System.out.println(SANGRIA + "└" + "─".repeat(ancho + 2) + "┘");
    }

    public static void exito(String mensaje) {
        System.out.println(SANGRIA + "✔ " + mensaje);
    }

    public static void error(String mensaje) {
        System.out.println(SANGRIA + "✖ " + mensaje);
    }

    public static void aviso(String mensaje) {
        System.out.println(SANGRIA + "⚠ " + mensaje);
    }

    public static void info(String mensaje) {
        System.out.println(SANGRIA + mensaje);
    }

    static String rellenar(String texto, int ancho) {
        return texto + " ".repeat(Math.max(0, ancho - texto.length()));
    }

    private static String centrar(String texto, int ancho) {
        int izquierda = Math.max(0, (ancho - texto.length()) / 2);
        return rellenar(" ".repeat(izquierda) + texto, ancho);
    }
}
