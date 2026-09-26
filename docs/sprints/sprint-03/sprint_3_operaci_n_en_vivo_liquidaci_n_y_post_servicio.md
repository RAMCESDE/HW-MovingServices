# 📍 SPRINT 3: Ejecución Operativa, Tracking en Vivo, Liquidación y Post-Servicio

**Objetivo del Sprint:** Habilitar la app del conductor para el seguimiento del servicio en tiempo real, cobro del saldo pendiente (90%), facturación digital y evaluación de calidad (CSAT).
**Capacidad Estimada:** 22 SP (2 Desarrolladores)

---

## 🆔 US-02: Recuperación de Contraseña
* **Puntos de Historia (SP):** 2
* **Prioridad:** Baja
* **Asignado a:** Dev 1 (Backend Tokens) / Dev 2 (UI Formulario)

### Descripción
* **Como** usuario registrado,
* **Quiero** solicitar la restauración de mi contraseña mediante un enlace enviado a mi correo electrónico,
* **Para** recuperar el acceso a mi cuenta en caso de olvido.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Envío exitoso de enlace de recuperación**
  * **Dado** que el usuario solicita la recuperación de contraseña en la app/web,
  * **Cuando** ingresa un correo electrónico registrado en el sistema,
  * **Entonces** el sistema genera un token seguro con vigencia de 15 minutos y envía el correo con las instrucciones para restablecerla.
* **Escenario 2: Enlace expirado**
  * **Dado** que el usuario hace clic en el enlace de recuperación después de transcurridos los 15 minutos de vigencia,
  * **Entonces** el sistema informa que el enlace ha expirado y le solicita generar una nueva petición.

---

## 🆔 US-04: Gestión del Perfil del Cliente
* **Puntos de Historia (SP):** 2
* **Prioridad:** Baja
* **Asignado a:** Dev 2 (Frontend)

### Descripción
* **Como** cliente,
* **Quiero** actualizar mis datos personales (nombre, teléfono de contacto y direcciones frecuentes),
* **Para** garantizar que la información de contacto y entrega sea exacta al momento de prestar el servicio.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Edición exitosa de número de teléfono y dirección**
  * **Dado** que el cliente se encuentra en la sección "Mi Perfil",
  * **Cuando** modifica su número de celular y guarda los cambios,
  * **Entonces** el sistema valida el formato telefónico y muestra una confirmación de actualización realizada.

---

## 🆔 US-10: Desactivación de Empleados
* **Puntos de Historia (SP):** 2
* **Prioridad:** Media
* **Asignado a:** Dev 1 (Backend) / Dev 2 (UI Admin)

### Descripción
* **Como** administrador,
* **Quiero** cambiar el estado de un empleado a "Inactivo" o "No Disponible",
* **Para** impedir que sea asignado a nuevos servicios de mudanza y restringir su acceso a la aplicación operativa.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Intentar asignar un empleado inactivo**
  * **Dado** que un ayudante tiene estado "Inactivo" en la base de datos,
  * **Cuando** el operador logístico busca personal para conformar la tripulación de una mudanza,
  * **Entonces** el empleado inactivo no aparece en la lista de recursos seleccionables.

---

## 🆔 US-15: Visualización de Agenda Logística (Calendario)
* **Puntos de Historia (SP):** 3
* **Prioridad:** Media
* **Asignado a:** Dev 2 (Frontend - Componente Calendario / Timeline)

### Descripción
* **Como** administrador,
* **Quiero** visualizar una agenda/calendario interactivo con todos los servicios programados, su estado y los recursos asignados,
* **Para** prevenir traslapes de horarios, sobrecargas de trabajo o sobreventa de la capacidad de la flota.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Consulta de disponibilidad en el calendario**
  * **Dado** que el administrador accede al Dashboard Logístico,
  * **Cuando** filtra la agenda por una fecha específica,
  * **Entonces** el sistema despliega la línea de tiempo del día con las franjas ocupadas por cada vehículo de la flota (`NPR`, `LUV`, `Motocarro`) y los servicios asignados.

---

## 🆔 US-16 / US-LOG-02: Seguimiento del Servicio en Tiempo Real
* **Puntos de Historia (SP):** 8
* **Prioridad:** Crítica
* **Asignado a:** Dev 1 (WebSockets / Firebase GPS) / Dev 2 (App Conductor & Vista Mapa Cliente)

### Descripción
* **Como** cliente y conductor,
* **Quiero** que el conductor actualice los hitos del traslado en la app (En desplazamiento, En origen, En destino, Finalizado),
* **Para** que el cliente pueda monitorear en tiempo real el progreso de su mudanza y la ubicación de su carga.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Actualización del estado por el conductor y notificación al cliente**
  * **Dado** que el conductor presiona el botón "Iniciando Desplazamiento a Origen" en su aplicación,
  * **Cuando** se registra el evento en el sistema,
  * **Entonces** la app del cliente cambia el estado a "En Trayecto" y activa la visualización en tiempo real en el mapa.

---

## 🆔 US-17: Liquidación de Saldo, Factura Digital y CSAT
* **Puntos de Historia (SP):** 5
* **Prioridad:** Alta
* **Asignado a:** Dev 1 (Generación Factura PDF & Pasarela 90%) / Dev 2 (UI Finalización & Estrellas CSAT)

### Descripción
* **Como** cliente,
* **Quiero** cancelar el saldo restante del servicio (90% sobrante) al verificar la entrega conforme, obtener mi factura digital y calificar la atención recibida,
* **Para** dar cierre formal al servicio y brindar retroalimentación sobre el desempeño de la tripulación.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Pago final y emisión de factura**
  * **Dado** que el conductor marca el servicio como "En destino / Descarga Completada",
  * **Cuando** el cliente procesa el pago del saldo restante (vía efectivo registrado por el conductor o pago digital en la App),
  * **Entonces** el sistema marca el servicio como "Finalizado", emite el recibo/factura digital y habilita el módulo de calificación post-servicio.
* **Escenario 2: Retroalimentación y calificación (CSAT)**
  * **Dado** que el servicio está finalizado y pagado al 100%,
  * **Cuando** el cliente califica el servicio con estrellas (1 a 5) y agrega comentarios sobre el conductor y ayudantes,
  * **Entonces** el sistema guarda la puntuación en el perfil operativo del personal y actualiza las métricas de calidad de la empresa.