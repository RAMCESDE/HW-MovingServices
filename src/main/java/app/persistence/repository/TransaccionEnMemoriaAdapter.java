package app.persistence.repository;

import app.service.outputports.TransaccionPort;

import java.util.function.Supplier;

/** En memoria no hay transacciones reales: la operación se ejecuta directamente. */
public class TransaccionEnMemoriaAdapter implements TransaccionPort {

    @Override
    public <T> T ejecutar(Supplier<T> operacion) {
        return operacion.get();
    }
}
