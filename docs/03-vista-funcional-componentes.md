# Vista 2: Funcional (Diagrama de Componentes)

**Objetivo:** mostrar los **módulos funcionales** del sistema, qué **interfaces ofrecen**
(*provided*, el "chupetín" ○) y cuáles **necesitan** de otros (*required*, el "socket" ◐).

## Componentes

| Componente | Responsabilidad | Ofrece (provided) | Requiere (required) | Historias |
|---|---|---|---|---|
| **Cliente REST** (Postman) | Envía las peticiones HTTP y muestra las respuestas JSON | | `API REST` | Todas |
| **API REST** (Controllers) | Recibe las peticiones HTTP/JSON, valida los datos y delega en los servicios | `API REST` | `IAutenticacion`, `IGestionFlota`, `IGestionVuelos`, `IBusquedaVuelos`, `IReservas` | Todas |
| **Autenticación y Seguridad** | Registro, login, roles, cifrado de contraseñas | `IAutenticacion` | `IRepositorios` | HU-01, HU-02 |
| **Gestión de Flota** | CRUD de aviones, estados, mapa de asientos | `IGestionFlota` | `IRepositorios` | HU-03, HU-04, HU-05 |
| **Gestión de Vuelos** | Programar, modificar y cancelar vuelos | `IGestionVuelos` | `IGestionFlota`, `INotificacion`, `IRepositorios` | HU-06, HU-07, HU-08 |
| **Búsqueda de Vuelos** | Filtrar vuelos por origen, destino y fecha | `IBusquedaVuelos` | `IRepositorios` | HU-09, HU-10 |
| **Selección de Asientos** | Disponibilidad y precio por asiento | `ISeleccionAsientos` | `IGestionVuelos` | HU-11 |
| **Reservas** | Crear, consultar y cancelar reservas | `IReservas` | `ISeleccionAsientos`, `IGestionVuelos`, `INotificacion`, `IRepositorios` | HU-12, HU-13, HU-14 |
| **Notificaciones** | Envío de correos | `INotificacion` | `SMTP` (externo) | HU-07, HU-12, HU-14 |
| **Persistencia** (Spring Data MongoDB) | Acceso a datos | `IRepositorios` | `MongoDB Driver` | Todas |
| **MongoDB** (externo) | Base de datos | `MongoDB Driver` | | |

## Diagrama

![VistaFuncional](../diagramas/Vista%20funcional.png)

