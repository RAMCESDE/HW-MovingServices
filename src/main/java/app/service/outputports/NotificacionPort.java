package app.service.outputports;

public interface NotificacionPort {

    void enviarCorreo(String destinatario, String asunto, String mensaje);
}
