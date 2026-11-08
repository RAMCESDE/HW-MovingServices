package app.service.outputports;

import java.util.function.Supplier;

public interface TransaccionPort {

    /** Ejecuta la operación de forma atómica: todo se guarda o nada se guarda. */
    <T> T ejecutar(Supplier<T> operacion);
}
