package app.persistence.repository;

import app.domain.Cuenta;
import app.service.outputports.CuentaRepositoryPort;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final Map<String, Cuenta> datos = new LinkedHashMap<>();

    @Override
    public Cuenta guardar(Cuenta cuenta) {
        datos.put(cuenta.getIdCuenta(), cuenta);
        return cuenta;
    }

    @Override
    public Optional<Cuenta> buscarPorId(String idCuenta) {
        return Optional.ofNullable(datos.get(idCuenta));
    }

    @Override
    public Optional<Cuenta> buscarPorEmail(String email) {
        return datos.values().stream().filter(c -> c.getEmail().equals(email)).findFirst();
    }

    @Override
    public Optional<Cuenta> buscarPorPerfil(String idPerfil) {
        return datos.values().stream().filter(c -> c.getIdPerfil().equals(idPerfil)).findFirst();
    }
}
