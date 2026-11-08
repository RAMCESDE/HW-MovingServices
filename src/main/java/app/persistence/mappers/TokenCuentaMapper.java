package app.persistence.mappers;

import app.domain.TokenCuenta;
import app.domain.enums.TipoTokenCuenta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TokenCuentaMapper implements RowMapper<TokenCuenta> {

    @Override
    public TokenCuenta mapRow(ResultSet rs) throws SQLException {
        TokenCuenta token = new TokenCuenta();
        token.setHashToken(rs.getString("hash_token"));
        token.setIdCuenta(rs.getString("id_cuenta"));
        token.setTipo(TipoTokenCuenta.valueOf(rs.getString("tipo")));
        token.setExpira(rs.getObject("expira", LocalDateTime.class));
        token.setUsado(rs.getBoolean("usado"));
        return token;
    }
}
