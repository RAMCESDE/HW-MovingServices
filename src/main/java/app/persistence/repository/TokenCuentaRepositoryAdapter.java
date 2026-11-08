package app.persistence.repository;

import app.domain.TokenCuenta;
import app.domain.enums.TipoTokenCuenta;
import app.service.outputports.TokenCuentaRepositoryPort;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class TokenCuentaRepositoryAdapter implements TokenCuentaRepositoryPort {

    private final Map<String, TokenCuenta> datos = new LinkedHashMap<>();

    @Override
    public TokenCuenta guardar(TokenCuenta token) {
        datos.put(token.getHashToken(), token);
        return token;
    }

    @Override
    public Optional<TokenCuenta> buscarPorHash(String hashToken) {
        return Optional.ofNullable(datos.get(hashToken));
    }

    @Override
    public void invalidarPendientes(String idCuenta, TipoTokenCuenta tipo) {
        datos.values().stream().filter(t -> t.getIdCuenta().equals(idCuenta) && t.getTipo() == tipo)
                .forEach(t -> t.setUsado(true));
    }
}
