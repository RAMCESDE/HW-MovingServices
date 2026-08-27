package app.domain;

public class Cliente extends Person {
    private String direccion;

    public Cliente() {}

    public Cliente(String nombre, String cedula, String telefono, String email, String direccion) {
        super(nombre, cedula, telefono, email);
        this.direccion = direccion;
    }

    public void registrarServicio() {}
}