package app.service.outputports;

import app.domain.Cuenta;

import java.util.Optional;

public interface CuentaRepositoryPort {

    Cuenta guardar(Cuenta cuenta);

    Optional<Cuenta> buscarPorId(String idCuenta);

    Optional<Cuenta> buscarPorEmail(String email);

    Optional<Cuenta> buscarPorPerfil(String idPerfil);
}
