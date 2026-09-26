# 🚀 SPRINT 1: Core Transaccional, Cotizador Inteligente y Pagos (MVP Base)

**Objetivo del Sprint:** Permitir que los usuarios puedan autenticarse, diligenciar el formulario de levantamiento, obtener una cotización automática instantánea y pagar el anticipo del 10% para reservar un servicio.
**Capacidad Estimada:** 22 SP (2 Desarrolladores)

---

## 🆔 US-01: Registro de Cliente
* **Puntos de Historia (SP):** 3
* **Prioridad:** Alta
* **Asignado a:** Dev 1 (Backend) / Dev 2 (Frontend)

### Descripción
* **Como** cliente nuevo,
* **Quiero** registrarme en la plataforma ingresando mi correo electrónico y una contraseña segura,
* **Para** crear mi cuenta y acceder a los servicios de cotización y reserva de mudanzas.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Registro exitoso**
  * **Dado** que el usuario está en la pantalla de registro y no tiene una cuenta existente,
  * **Cuando** ingresa un correo válido, una contraseña que cumple con los requisitos de seguridad y acepta los términos y condiciones,
  * **Entonces** el sistema crea la cuenta, envía un correo de verificación y redirige al usuario a la pantalla de confirmación.
* **Escenario 2: Intento de registro con correo ya existente**
  * **Dado** que el correo `cliente@ejemplo.com` ya se encuentra registrado en el sistema,
  * **Cuando** un nuevo usuario intenta registrarse usando `cliente@ejemplo.com`,
  * **Entonces** el sistema muestra un mensaje indicando que el correo ya está registrado y ofrece el enlace para iniciar sesión.

### Reglas de Negocio
* La contraseña debe tener como mínimo 8 caracteres, incluir al menos una letra mayúscula, un número y un carácter especial.
* El correo electrónico debe ser único dentro de la plataforma.

---

## 🆔 US-03: Iniciar Sesión y Control de Acceso por Rol
* **Puntos de Historia (SP):** 3
* **Prioridad:** Alta
* **Asignado a:** Dev 1 (Backend) / Dev 2 (Frontend)

### Descripción
* **Como** usuario registrado (Cliente, Administrador, Conductor/Operador Logístico),
* **Quiero** autenticarme con mis credenciales en la plataforma,
* **Para** acceder a la interfaz y funcionalidades correspondientes a mi rol asignado.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Inicio de sesión correcto como Administrador**
  * **Dado** que un usuario con rol "Administrador" ingresa credenciales válidas,
  * **Cuando** presiona "Iniciar Sesión",
  * **Entonces** el sistema lo redirige al Dashboard de Gestión Logística, Flota y Personal.
* **Escenario 2: Inicio de sesión correcto como Cliente**
  * **Dado** que un cliente ingresa credenciales válidas,
  * **Cuando** presiona "Iniciar Sesión",
  * **Entonces** el sistema lo redirige al panel principal para cotizar o ver sus reservas activas.

---

## 🆔 US-11 / US-CLI-01: Cotización Instantánea (Algoritmo de Precios)
* **Puntos de Historia (SP):** 8
* **Prioridad:** Crítica
* **Asignado a:** Dev 1 (Backend - Algoritmo y Geolocalización) / Dev 2 (Frontend - Formulario Multipaso)

### Descripción
* **Como** cliente,
* **Quiero** diligenciar un formulario de levantamiento (origen, destino, tipo de servicio [acarreo o mudanza], cantidad de objetos, número de pisos, presencia de ascensor y requerimiento de ayudantes),
* **Para** recibir una cotización automática y transparente en cuestión de segundos mediante el algoritmo de precios.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Cotización automática exitosa**
  * **Dado** que el cliente completa todos los datos obligatorios del formulario de levantamiento,
  * **Cuando** presiona "Calcular Cotización",
  * **Entonces** el sistema ejecuta el algoritmo de precios y despliega en pantalla el desglose del precio estimado indicando tarifa base, recargo por distancia, pisos/complejidad y personal adicional.
* **Escenario 2: Opción de guardar borrador**
  * **Dado** que el cliente recibe la cotización instantánea pero no desea agendar de inmediato,
  * **Cuando** selecciona la opción "Guardar Borrador",
  * **Entonces** el sistema almacena los parámetros de la solicitud para que pueda retomarla en su sesión.

### Reglas de Negocio
* Si el trayecto implica subir/bajar pisos por escalera (sin ascensor), se aplica un recargo proporcional al número de pisos y al tipo de carga.
* Se contemplan variaciones de tarifa según la categoría de vehículo sugerido (`NPR`, `LUV`, `Motocarro`).

---

## 🆔 US-12 / US-13 / US-CLI-02: Reserva, Calendario y Pago del Anticipo (10%)
* **Puntos de Historia (SP):** 8
* **Prioridad:** Crítica
* **Asignado a:** Dev 1 (Backend - Pasarela & Webhooks) / Dev 2 (Frontend - UX Pago y Calendario)

### Descripción
* **Como** cliente,
* **Quiero** seleccionar la fecha y hora disponible para mi mudanza y realizar el pago del anticipo del 10% a través de una pasarela de pagos,
* **Para** asegurar la reserva del servicio y generar la orden en el panel logístico de la empresa.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Reserva y pago de anticipo exitoso**
  * **Dado** que el cliente aceptó una cotización previa y selecciona un bloque de fecha/hora en el calendario interactivo,
  * **Cuando** realiza exitosamente el pago del anticipo (mínimo el 10% del total cotizado) a través de tarjeta de crédito, débito o transferencia en la pasarela,
  * **Entonces** el sistema cambia el estado del servicio a "Reservado", emite un comprobante provisional digital y lo encola en el dashboard del Administrador.
* **Escenario 2: Rechazo de transacción de pago**
  * **Dado** que la pasarela de pagos rechaza la transacción del cliente por fondos insuficientes u otra razón,
  * **Entonces** el sistema notifica el fallo en la transacción, no efectúa la reserva en la agenda y permite intentar con otro método de pago.