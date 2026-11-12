package app.domain;

public class Persona {

    private String nombre;
    private String cedula;
    private String telefono;
    private String email;

    public Persona() {
    }

    public Persona(String nombre, String cedula, String telefono, String email) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.telefono = telefono;
        this.email = email;
    }

    public void actualizarPerfil(String nombre, String telefono) {
        if (nombre != null) {
            this.nombre = nombre;
        }
        if (telefono != null) {
            this.telefono = telefono;
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
