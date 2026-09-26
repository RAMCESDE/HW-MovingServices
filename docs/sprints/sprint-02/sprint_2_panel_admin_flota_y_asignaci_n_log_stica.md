# 🚛 SPRINT 2: Módulo Administrador, Gestión de Flota y Asignación Logística

**Objetivo del Sprint:** Construir la plataforma para el administrador/operador logístico, permitiendo gestionar el inventario de la flota, el personal operativo, controlar restricciones de Pico y Placa y asignar recursos a las reservas entrantes.
**Capacidad Estimada:** 24 SP (2 Desarrolladores)

---

## 🆔 US-05 / US-ADM-01: Registro y Clasificación de Vehículos
* **Puntos de Historia (SP):** 5
* **Prioridad:** Alta
* **Asignado a:** Dev 1 (Backend CRUD) / Dev 2 (UI Admin)

### Descripción
* **Como** administrador,
* **Quiero** registrar vehículos ingresando tipo (`NPR`, `LUV`, `Motocarro`), placa, modelo, capacidad de carga y vigencia de documentos,
* **Para** mantener actualizado el inventario de la flota y conocer su disponibilidad para la asignación de servicios.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Registro completo de vehículo de la flota**
  * **Dado** que el administrador completa los campos de placa, tipo de vehículo (`NPR`, `LUV` o `Motocarro`), modelo, capacidad de carga en toneladas/m³ y las fechas de vencimiento de SOAT y Tecnomecánica,
  * **Cuando** guarda la información,
  * **Entonces** el vehículo queda registrado en el sistema con estado inicial "Disponible".
* **Escenario 2: Validación de tipo de vehículo de la flota**
  * **Dado** que se está registrando un nuevo vehículo,
  * **Cuando** se selecciona la categoría,
  * **Entonces** solo se permite escoger entre las opciones configuradas: `NPR`, `LUV` y `Motocarro`.

### Reglas de Negocio
* La flota inicial comprende los tipos especificados: 2 NPR, 2 LUV y Motocarro.
* Un vehículo no puede ser registrado con placas duplicadas.

---

## 🆔 US-07: Alertas de Vencimiento de Documentación Vehicular
* **Puntos de Historia (SP):** 3
* **Prioridad:** Media
* **Asignado a:** Dev 1 (Backend Cron Job & Mailer)

### Descripción
* **Como** administrador,
* **Quiero** que el sistema emita alertas preventivas sobre la fecha de vencimiento del SOAT y la Tecnomecánica de cada vehículo,
* **Para** evitar la operación de vehículos no reglamentarios y gestionar su renovación a tiempo.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Alerta por documento próximo a vencer**
  * **Dado** que el SOAT de un vehículo NPR está a 15 días o menos de vencer,
  * **Cuando** el administrador ingresa al Dashboard o revisa el estado del vehículo,
  * **Entonces** el sistema muestra una alerta visual en amarillo ("Documento Próximo a Vencer").
* **Escenario 2: Bloqueo de vehículo por documento vencido**
  * **Dado** que la Tecnomecánica de un vehículo se encuentra vencida a la fecha actual,
  * **Entonces** el sistema cambia automáticamente el estado del vehículo a "Inhabilitado por Documentación" y prohíbe su asignación a cualquier servicio logístico.

---

## 🆔 US-08: Parametrización de Restricciones de Pico y Placa
* **Puntos de Historia (SP):** 3
* **Prioridad:** Alta
* **Asignado a:** Dev 1 (Lógica de Negocio) / Dev 2 (Filtro en UI)

### Descripción
* **Como** administrador,
* **Quiero** parametrizar las fechas, días y dígitos de Pico y Placa aplicables a la ciudad de operación,
* **Para** que la app restrinja automáticamente la programación de viajes en vehículos sancionables en dichos horarios.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Filtro automático en programación logística**
  * **Dado** que un vehículo tiene restricción de Pico y Placa el día Lunes según el último dígito de su placa,
  * **Cuando** el administrador intenta asignarlo a un servicio agendado para ese mismo Lunes en horario restringido,
  * **Entonces** el sistema bloquea la asignación y despliega un mensaje indicando la restricción legal del vehículo.

---

## 🆔 US-09 / US-ADM-02: Registro y Perfil de Empleados
* **Puntos de Historia (SP):** 5
* **Prioridad:** Alta
* **Asignado a:** Dev 1 (Backend CRUD Empleados) / Dev 2 (UI Gestión de Personal)

### Descripción
* **Como** administrador,
* **Quiero** registrar el personal de la empresa (5 consultores y 5 ayudantes) capturando sus datos sociodemográficos, tipo de licencia, EPS y dotación,
* **Para** gestionar la disponibilidad de personal calificado y asignarlo a los servicios de acarreo o mudanza según su perfil.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Registro de un Conductor o Ayudante**
  * **Dado** que el administrador ingresa los datos completos (nombre, documento, teléfono, edad, sexo, estado civil, EPS, tipo de licencia y talla/dotación de equipo),
  * **Cuando** guarda la ficha del empleado,
  * **Entonces** el sistema registra al trabajador asociándole el rol operativo correspondiente ("Consultor/Conductor" o "Ayudante/Cargador").

---

## 🆔 US-14 / US-LOG-01: Asignación Logística de Recursos
* **Puntos de Historia (SP):** 8
* **Prioridad:** Crítica
* **Asignado a:** Dev 1 (Backend Engine de Asignación) / Dev 2 (UI Dashboard Logístico)

### Descripción
* **Como** administrador / operador logístico,
* **Quiero** revisar las reservas del panel, validar su factibilidad y asignar el vehículo (`NPR`, `LUV` o `Motocarro`), el conductor y los ayudantes necesarios,
* **Para** garantizar la preparación operativa del servicio y confirmar los detalles al cliente y al personal.

### Criterios de Aceptación (Gherkin)
* **Escenario 1: Asignación de tripulación y vehículo sin conflictos**
  * **Dado** que el administrador selecciona una reserva en estado "Reservado" para la fecha X,
  * **Cuando** asigna un vehículo disponible que cumple con la capacidad de la carga, un conductor con licencia vigente y el número solicitado de ayudantes libres,
  * **Entonces** el servicio cambia a estado "Confirmado" y el sistema notifica automáticamente los datos de la ruta al equipo y la información de seguridad (placa, foto/datos del personal) al cliente.