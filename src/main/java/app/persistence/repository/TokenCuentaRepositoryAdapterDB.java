package app.persistence.repository;

import app.domain.TokenCuenta;
import app.domain.enums.TipoTokenCuenta;
import app.persistence.dbconnection.JdbcTemplate;
import app.persistence.mappers.TokenCuentaMapper;
import app.service.outputports.TokenCuentaRepositoryPort;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class TokenCuentaRepositoryAdapterDB implements TokenCuentaRepositoryPort {

    private final JdbcTemplate jdbc;

    public TokenCuentaRepositoryAdapterDB(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final TokenCuentaMapper MAPPER = new TokenCuentaMapper();

    @Override
    public TokenCuenta guardar(TokenCuenta t) {
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("hash_token", t.getHashToken());
        f.put("id_cuenta", t.getIdCuenta());
        f.put("tipo", t.getTipo());
        f.put("expira", t.getExpira());
        f.put("usado", t.isUsado());
        jdbc.upsert("token_cuenta", f, "hash_token");
        return t;
    }

    @Override
    public Optional<TokenCuenta> buscarPorHash(String hashToken) {
        return jdbc.queryOne("SELECT * FROM token_cuenta WHERE hash_token = ?", MAPPER, hashToken);
    }

    @Override
    public void invalidarPendientes(String idCuenta, TipoTokenCuenta tipo) {
        jdbc.update("UPDATE token_cuenta SET usado = TRUE WHERE id_cuenta = ? AND tipo = ?", idCuenta, tipo);
    }
}
