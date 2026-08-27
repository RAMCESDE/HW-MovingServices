package app.domain;

public class Cliente extends Persona {
    private String direccion;

    public Cliente() {}

    public Cliente(String nombre, String cedula, String telefono, String email, String direccion) {
        super(nombre, cedula, telefono, email);
        this.direccion = direccion;
    }

    public void registrarServicio() {}

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}

