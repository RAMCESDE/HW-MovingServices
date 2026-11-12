# HW Moving Services: backend

Capa backend en Java del sistema de mudanzas y acarreos de HW Moving Services (equipo RAM, CESDE).
Sigue la estructura de TicketMaxV1: arquitectura por capas con puertos y adaptadores, y consola como interfaz.

## Cómo ejecutarlo

Requiere JDK 21 o superior y Maven. Clase principal: `app.Application`.

```bash
mvn compile exec:java -Dexec.mainClass=app.Application
mvn test
```

Sin configurar nada, los datos quedan en memoria y se pierden al cerrar. Administrador inicial:
`admin@hwmoving.com` / `Admin123*`.

### Con MySQL

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

En `src/main/resources/application.properties` cambie `persistence.mode=mysql` y sus credenciales. También se pueden
pasar como variables de entorno, sin dejar contraseñas en el repositorio: `HW_PERSISTENCE_MODE`, `HW_DB_URL`,
`HW_DB_USER`, `HW_DB_PASSWORD`, `HW_ADMIN_EMAIL`, `HW_ADMIN_PASSWORD`.

Si MySQL responde `Public Key Retrieval is not allowed`, agregue `?allowPublicKeyRetrieval=true&useSSL=false` a la URL
(solo en un entorno local).

Para correr las pruebas de servicios contra MySQL, defina `HW_TEST_DB_URL`, `HW_TEST_DB_USER` y `HW_TEST_DB_PASSWORD`.
**Esas pruebas borran el contenido de las tablas**: use una base dedicada a pruebas.

## Arquitectura

```
ui ──> view ──> service/inputports  <── service (casos de uso) ──> service/outputports <── persistence
                                               │                                               │
                                               └───────────────> domain <──────────────────────┘
configuration: crea los adaptadores y los conecta (raíz de composición)
```

| Capa | Contenido |
|---|---|
| `configuration` | `Config`, `Servicios`, `Repositorios`, `AppProperties`. Únicas clases que conocen las implementaciones. |
| `domain` | Entidades y reglas propias (`Reserva`, `Vehiculo`, `RestriccionPicoPlaca`…), enums y excepciones. No depende de nada. |
| `persistence` | `repository`: adaptador en memoria (`XRepositoryAdapter`) y MySQL (`XRepositoryAdapterDB`) por cada puerto. `mappers`, `dbconnection`. `external`: correo, pasarela, distancia y hash de contraseñas. |
| `service` | `inputports` (casos de uso), `outputports` (lo que el servicio necesita de afuera), `XServiceAdapter`, `helpers`, `validators`. |
| `ui` | `CliUserInterface`: menús por rol y manejo de errores. |
| `view` | Una vista por módulo: lee datos y muestra resultados. |

`ArquitecturaTest` falla si una capa importa algo que no debe.

### SOLID

- **S:** `Cuenta` guarda credenciales y `Persona` datos personales; cada servicio atiende un solo módulo.
- **O:** otro algoritmo de precios se agrega implementando `EstrategiaPrecio`, sin tocar `CotizacionServiceAdapter`.
- **L:** los adaptadores en memoria y MySQL son intercambiables; la misma suite de pruebas pasa con ambos.
- **I:** puertos pequeños y separados, por ejemplo `FlotaServiceInterface`, `DocumentoVehiculoServiceInterface` y `PicoPlacaServiceInterface`.
- **D:** los servicios reciben interfaces por constructor; hasta el reloj se inyecta para poder probar vencimientos.

### Diferencias frente a TicketMaxV1

- La lectura por `Scanner` pasó de `service/validators` a `view/helpers`, para que los servicios no dependan de la consola.
- `CliUserInterface` ya no abre la conexión a la base de datos; solo `Config` conoce esa dependencia.
- Las credenciales de la base de datos salen del código fuente y van a configuración.
- Se agregó `persistence/external` para los adaptadores que no son base de datos, conservando las seis capas pedidas.

## Trazabilidad de historias

| Historia | Caso de uso | Vista (menú) | Pruebas |
|---|---|---|---|
| US-01 Registro | `CuentaServiceAdapter.registrarCliente`, `verificarCorreo` | Inicio 1 y 3 | `CuentaServiceTest` |
| US-02 Recuperar contraseña | `solicitarRecuperacion`, `restablecerContrasena` | Inicio 4 y 5 | `CuentaServiceTest` |
| US-03 Login y roles | `iniciarSesion`, `ControlAcceso` | Inicio 2 | `CuentaServiceTest` |
| US-04 Perfil | `ClienteServiceAdapter` | Cliente 1 y 2 | `CuentaServiceTest` |
| US-05 Vehículos | `FlotaServiceAdapter.registrarVehiculo` | Admin 1 y 2 | `FlotaYPersonalServiceTest` |
| US-06 Conductores por vehículo | `asignarConductor`, `retirarConductor` | Admin 3 y 4 | `FlotaYPersonalServiceTest` |
| US-07 Documentos y alertas | `DocumentoVehiculoServiceAdapter` | Admin 5 y 6, y al iniciar sesión | `FlotaYPersonalServiceTest`, `VehiculoTest` |
| US-08 Pico y Placa | `PicoPlacaServiceAdapter` | Admin 7 a 10 | `FlotaYPersonalServiceTest`, `VehiculoTest` |
| US-09 Empleados | `EmpleadoServiceAdapter.registrarEmpleado` | Admin 11 y 12 | `FlotaYPersonalServiceTest` |
| US-10 Desactivar empleado | `cambiarEstado` | Admin 13 | `FlotaYPersonalServiceTest`, `OperacionServiceTest` |
| US-11 Cotización | `CotizacionServiceAdapter.cotizar`, `guardarBorrador` | Cliente 3 y 4 | `CotizacionYReservaServiceTest` |
| US-12 Aceptar o rechazar | `aceptar`, `rechazar` | Cliente 3 y 5 | `CotizacionYReservaServiceTest` |
| US-13 Método de pago y reserva | `ReservaServiceAdapter` | Cliente 6 y 7, Admin 14 | `CotizacionYReservaServiceTest` |
| US-14 Programar servicio | `ProgramacionServiceAdapter.programar` | Admin 15 | `OperacionServiceTest` |
| US-15 Agenda | `consultarAgenda` y validación de traslapes | Admin 16 | `OperacionServiceTest` |
| US-16 Estado del servicio | `SeguimientoServiceAdapter` | Cliente 8, Conductor 1 y 2, Auxiliar 1 | `OperacionServiceTest` |
| US-17 Liquidación y calificación | `LiquidacionServiceAdapter`, `EvaluacionServiceAdapter` | Cliente 9 a 11, Conductor 3, Admin 18 | `OperacionServiceTest` |

## Decisiones donde los documentos no coincidían o no precisaban

Conviene validarlas con el equipo.

- **Anticipo en efectivo (US-13):** el backlog permite efectivo y el documento del Sprint 1 solo pasarela. Se aceptan ambos: en efectivo la reserva queda *Pendiente de anticipo* hasta que el administrador registra el dinero, y solo entonces se puede programar.
- **Estados del servicio (US-16):** internamente se usan los hitos del Sprint 3 (En desplazamiento, En origen, En destino, Finalizado) y el cliente ve las tres etapas del backlog (Pendiente, En Trayecto, Finalizada).
- **Finalizar (US-16 y US-17):** el conductor llega hasta *En destino*; el servicio pasa a *Finalizado* al pagarse el saldo.
- **Pico y Placa (US-08):** el backlog dice "por vehículo" y el Sprint 2 "días y dígitos de la ciudad". La regla se define por día, dígitos, franja y vigencia, y se puede consultar por vehículo.
- **Cuentas de empleados (US-09 y US-10):** cada empleado recibe una cuenta con contraseña temporal. *Inactivo* bloquea el acceso; *No disponible* solo impide nuevas asignaciones. Desactivar no modifica servicios ya programados.
- **Capacidad (US-14):** se valida por volumen en m³. El peso en kg queda registrado pero no se compara, porque la cotización no pide peso.

## Valores de ejemplo que debe confirmar la empresa

- **Tarifas, factores y capacidades** en `application.properties`, y factores de carga en `TipoCarga`: son inventados.
- **Volúmenes de `Enser`** y duración estimada en `EstrategiaPrecioPorVehiculo`: son estimaciones.
- **Reglas de Pico y Placa:** no vienen cargadas; el administrador las crea según el decreto vigente.

## Simulado o pendiente

| Tema | Estado actual | Para hacerlo real |
|---|---|---|
| Correos | Se imprimen en consola | Implementar `NotificacionPort` con SMTP |
| Pasarela de pagos | Simulada, sin cobros reales; `tok_rechazado` simula un rechazo | Implementar `PasarelaPagoPort` (Wompi, PayU o Stripe) y sus webhooks |
| Distancia | Línea recta entre municipios × 1,35; 6 km dentro del mismo municipio | Implementar `DistanciaPort` con un servicio de mapas |
| Seguimiento | Estado actual, historial y correo en cada hito | GPS, mapa y WebSockets |
| Factura | Recibo digital en texto | PDF y facturación electrónica ante la DIAN |
| Autenticación | Sesión en memoria dentro de la consola | JWT cuando exista una API REST |
| API para el frontend | No incluida, igual que en TicketMaxV1 | Agregar controladores REST en `ui` que llamen los mismos `inputports` |
| Direcciones frecuentes (US-04) | Una sola dirección por cliente | Tabla de direcciones del cliente |
| Cancelar reserva | Está en el diagrama, sin historia ni política de devolución | Definir la regla de negocio |
| Revisión automática de documentos | Al iniciar sesión el administrador, al listar y al programar | Tarea programada que además envíe correos |
