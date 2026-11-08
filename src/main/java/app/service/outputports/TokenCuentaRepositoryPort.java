package app.service.outputports;

import app.domain.TokenCuenta;
import app.domain.enums.TipoTokenCuenta;

import java.util.Optional;

public interface TokenCuentaRepositoryPort {

    TokenCuenta guardar(TokenCuenta token);

    Optional<TokenCuenta> buscarPorHash(String hashToken);

    void invalidarPendientes(String idCuenta, TipoTokenCuenta tipo);
}
