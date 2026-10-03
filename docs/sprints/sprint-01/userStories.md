# 🚀 SPRINT 1: Core Transaccional, Cotizador Inteligente y Pagos (MVP Base)

**Objetivo del Sprint:** Permitir que los usuarios puedan autenticarse, diligenciar el formulario de levantamiento, obtener una cotización automática instantánea y pagar el anticipo del 10% para reservar un servicio.

---

## Tabla de Historias de Usuario

| ID Historia | Descripción de la Historia de Usuario | Prioridad | Dificultad (SP) | Justificación de la Dificultad |
| --- | --- | --- | --- | --- |
| US-01 | Registro de Cliente | Alta | 3 SP | Formulario básico, validaciones de seguridad, hashing de contraseña y envío de emails de confirmación. |
| US-03 | Iniciar Sesión y Control de Acceso por Rol | Alta | 3 SP | Autenticación JWT / OAuth2, gestión de tokens y middleware de autorización por roles (Cliente/Admin/Conductor). |
| US-11 / US-CLI-01 | Cotización Instantánea (Algoritmo de Precios) | Crítica | 8 SP | Complejidad Alta: Cálculo de rutas/distancias, lógica para recargos por tipo de vehículo (NPR, LUV, Motocarro), número de pisos, ascensores y cargadores adicionales. |
| US-12 / US-13 / US-CLI-02 | Reserva, Calendario y Pago del Anticipo (10%) | Crítica | 8 SP | Integración con Pasarela de Pagos (Stripe/Wompi/PayU) para procesar el anticipo del 10%, manejo de webhooks y emisión de comprobante provisional. |

---

**Total Puntos de Historia (Sprint 1):** 22 SP

**Entregable funcional al final del Sprint:** El flujo transaccional del cliente desde el registro hasta la reserva pagada con el 10% de anticipo.