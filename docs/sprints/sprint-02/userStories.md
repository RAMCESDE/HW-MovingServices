# 🚛 SPRINT 2: Módulo Administrador, Gestión de Flota y Asignación Logística

**Objetivo del Sprint:** Construir la plataforma para el administrador/operador logístico, permitiendo gestionar el inventario de la flota, el personal operativo, controlar restricciones de Pico y Placa y asignar recursos a las reservas entrantes.

---

## Tabla de Historias de Usuario

| ID Historia | Descripción de la Historia de Usuario | Prioridad | Dificultad (SP) | Justificación de la Dificultad |
| --- | --- | --- | --- | --- |
| US-05 / US-ADM-01 | Registro y Clasificación de Vehículos (NPR, LUV, Motocarro) | Alta | 5 SP | CRUD de vehículos, categorización por tipo, almacenamiento de documentos y reglas de disponibilidad. |
| US-07 | Alertas de Vencimiento de Documentación | Media | 3 SP | Lógica de tareas programadas (Cron Jobs) para verificar fechas de vencimiento de SOAT/Tecnomecánica y emitir alertas/bloqueos. |
| US-08 | Parametrización de Pico y Placa | Alta | 3 SP | Regla de negocio para restringir vehículos según el último dígito de la placa y días de la semana. |
| US-09 / US-ADM-02 | Registro y Perfil de Empleados (5 conductores, 5 ayudantes) | Alta | 5 SP | CRUD de personal, roles operativos, licencias de conducción, EPS e inventario de dotaciones. |
| US-14 / US-LOG-01 | Asignación Logística Inteligente | Crítica | 8 SP | Complejidad Alta: Panel de control logístico que cruza disponibilidad de flota, Pico y Placa y disponibilidad de personal para validar y confirmar un servicio. |

---

**Total Puntos de Historia (Sprint 2):** 24 SP