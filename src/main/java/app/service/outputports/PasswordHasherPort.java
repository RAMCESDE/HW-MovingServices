package app.service.outputports;

public interface PasswordHasherPort {

    String hashear(String password);

    boolean coincide(String password, String hash);
}
