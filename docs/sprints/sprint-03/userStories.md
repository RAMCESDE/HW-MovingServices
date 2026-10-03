# 📍 SPRINT 3: Ejecución Operativa, Tracking en Vivo, Liquidación y Post-Servicio

**Objetivo del Sprint:** Habilitar la app del conductor para el seguimiento del servicio en tiempo real, cobro del saldo pendiente (90%), facturación digital y evaluación de calidad (CSAT).

---

## Tabla de Historias de Usuario

| ID Historia | Descripción de la Historia de Usuario | Prioridad | Dificultad (SP) | Justificación de la Dificultad |
| --- | --- | --- | --- | --- |
| US-02 | Recuperación de Contraseña | Baja | 2 SP | Generación de tokens temporales de restablecimiento y envío por correo electrónico. |
| US-04 | Gestión del Perfil del Cliente | Baja | 2 SP | Edición de datos de contacto y direcciones preferidas. |
| US-10 | Desactivación de Empleados | Media | 2 SP | Inactivación lógica de personal para restringir acceso e invisibilizarlos en la asignación. |
| US-15 | Visualización de Agenda Logística (Calendario) | Media | 3 SP | Vista interactiva tipo calendario/Gantt para ver la ocupación diaria de la flota. |
| US-16 / US-LOG-02 | Seguimiento del Servicio en Tiempo Real | Crítica | 8 SP | Complejidad Alta: App Conductor con estados de ruta, integración con geolocalización GPS y actualización vía WebSockets / Firebase para el mapa del cliente. |
| US-17 | Liquidación de Saldo, Factura Digital y CSAT | Alta | 5 SP | Pago del 90% restante, generación de PDF/Factura digital y sistema de calificación por estrellas y comentarios. |

---

**Total Puntos de Historia (Sprint 3):** 22 SP