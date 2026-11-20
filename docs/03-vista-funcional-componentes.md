# Vista 2: Funcional (Diagrama de Componentes)

**Objetivo:** mostrar los **módulos funcionales** del sistema, qué **interfaces ofrecen**
(*provided*, el círculo "chupetín" ○) y cuáles **usan** de otros módulos (*required*, el
semicírculo "socket" ◐). Las notas de cada interfaz indican qué requisitos (RF) e historias
(HU) cubre.

## Componentes

| Componente | Responsabilidad | Ofrece (provided) | Requiere (required) |
|---|---|---|---|
| **Autenticación** | Registro, login, roles y cifrado de contraseñas | `iAutenticacion` | |
| **Gestión de flota** | Registrar aviones, cambiar su estado y configurar el mapa de asientos | `iGestionFlota`, `iDisponibilidadAvion` | |
| **Gestión de vuelos** | Programar, modificar, retrasar y cancelar vuelos; panel de ocupación | `iGestionVuelos`, `iConsultaVuelos` | `iDisponibilidadAvion`, `iNotificacion` |
| **Búsqueda de vuelos** | Buscar por origen, destino y fecha; detalle y precio por clase | `iBusquedaVuelos` | `iConsultaVuelos` |
| **Selección de asientos** | Mapa de asientos con disponibilidad y precio de cada uno | `iMapaAsientos` | `iConsultaVuelos` |
| **Reservas** | Crear, consultar y cancelar reservas | `iGestionReservas` | `iConsultaVuelos`, `iMapaAsientos`, `iNotificacion` |
| **Notificaciones** | Arma y envía los correos a los pasajeros | `iNotificacion` | `iEnvioCorreo` |
| **Servicio de correo** `<<Sistema Externo>>` | Servidor SMTP | `iEnvioCorreo` | |

## Interfaces

| Interfaz | La ofrece | La usa | RF | HU |
|---|---|---|---|---|
| `iAutenticacion` | Autenticación | Administrador, Pasajero / Visitante | RF-01, RF-02 | HU-01, HU-02 |
| `iGestionFlota` | Gestión de flota | Administrador | RF-03 a RF-05 | HU-03 a HU-05 |
| `iDisponibilidadAvion` | Gestión de flota | Gestión de vuelos | RF-04, RF-06 | HU-04, HU-06 |
| `iGestionVuelos` | Gestión de vuelos | Administrador | RF-06 a RF-08 | HU-06 a HU-08 |
| `iNotificacion` | Notificaciones | Gestión de vuelos, Reservas | RF-15 | HU-07, HU-12, HU-14 |
| `iEnvioCorreo` | Servicio de correo | Notificaciones | RF-15 | |
| `iConsultaVuelos` | Gestión de vuelos | Búsqueda, Selección de asientos, Reservas | RF-09 a RF-12 | HU-09 a HU-12 |
| `iBusquedaVuelos` | Búsqueda de vuelos | Pasajero / Visitante | RF-09, RF-10 | HU-09, HU-10 |
| `iMapaAsientos` | Selección de asientos | Pasajero / Visitante, Reservas | RF-11 | HU-11 |
| `iGestionReservas` | Reservas | Pasajero / Visitante | RF-12 a RF-14 | HU-12 a HU-14 |

Las historias HU-15 a HU-24 son consultas y variantes de estas mismas funciones, así que usan
las mismas interfaces (por ejemplo, HU-16 *Listar la flota* va por `iGestionFlota` y HU-23
*Detalle de una reserva* por `iGestionReservas`).

## Diagrama

![VistaFuncional](../diagramas/Vista%20Funcional.png)
