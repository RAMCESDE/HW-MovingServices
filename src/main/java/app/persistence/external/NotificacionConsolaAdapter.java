package app.persistence.external;

import app.service.outputports.NotificacionPort;

/** Muestra los correos en consola. Para enviarlos de verdad se implementa NotificacionPort con SMTP. */
public class NotificacionConsolaAdapter implements NotificacionPort {

    @Override
    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        System.out.println("\n[CORREO SIMULADO] Para: " + destinatario + "\nAsunto: " + asunto + "\n" + mensaje + "\n");
    }
}
