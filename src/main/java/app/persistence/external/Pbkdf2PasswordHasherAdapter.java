package app.persistence.external;

import app.service.outputports.PasswordHasherPort;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Hash de contraseñas con PBKDF2 y sal aleatoria, sin dependencias externas. */
public class Pbkdf2PasswordHasherAdapter implements PasswordHasherPort {

    private static final int ITERACIONES = 210_000;
    private static final int BITS = 256;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String hashear(String password) {
        byte[] sal = new byte[16];
        random.nextBytes(sal);
        return ITERACIONES + ":" + Base64.getEncoder().encodeToString(sal) + ":"
                + Base64.getEncoder().encodeToString(derivar(password, sal, ITERACIONES));
    }

    @Override
    public boolean coincide(String password, String hash) {
        String[] partes = hash.split(":");
        byte[] esperado = Base64.getDecoder().decode(partes[2]);
        byte[] calculado = derivar(password, Base64.getDecoder().decode(partes[1]), Integer.parseInt(partes[0]));
        return MessageDigest.isEqual(esperado, calculado);
    }

    private byte[] derivar(String password, byte[] sal, int iteraciones) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), sal, iteraciones, BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No fue posible procesar la contraseña", e);
        }
    }
}
