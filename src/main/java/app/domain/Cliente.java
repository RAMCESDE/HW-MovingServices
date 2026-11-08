package app.domain;

public class Cliente extends Persona {

    private String idCliente;
    private String direccion;

    public Cliente() {
    }

    public Cliente(String nombre, String cedula, String telefono, String email, String direccion) {
        super(nombre, cedula, telefono, email);
        this.direccion = direccion;
    }

    public void actualizarPerfil(String nombre, String telefono, String direccion) {
        super.actualizarPerfil(nombre, telefono);
        if (direccion != null) {
            this.direccion = direccion;
        }
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
