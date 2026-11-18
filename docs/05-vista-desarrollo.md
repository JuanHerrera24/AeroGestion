# Vista 4: Desarrollo (Diagrama de Clases por Capas)

**Objetivo:** mostrar cómo se organiza el código en paquetes y cómo dependen entre sí.
Las dependencias van siempre en un sentido: **controller → service → repository → model**.

![VistaDesarrollo](../diagramas/Vista%20de%20Desarrollo.png)

## Paquetes (`com.aerogestion`)

| Paquete | Clases | Responsabilidad |
|---|---|---|
| `controller` | `AuthController`, `VueloController`, `ReservaController` | Endpoints REST públicos y del pasajero |
| `controller.admin` | `AdminController`, `AdminAvionController`, `AdminVueloController` | Endpoints del administrador |
| `dto` | `VueloForm`, `AvionForm`, `RegistroForm`, `ReservaRequest`, `VueloDTO`, `ReservaDTO`, `AvionDTO`, `UsuarioDTO`, `ErrorDTO`… | Datos JSON de entrada y salida |
| `service` | `IAuthService`, `IAvionService`, `IVueloService`, `IBusquedaService`, `IReservaService` | Interfaces de la lógica de negocio |
| `service.impl` | `AuthServiceImpl`, `AvionServiceImpl`, `VueloServiceImpl`, `BusquedaServiceImpl`, `ReservaServiceImpl` | Reglas de negocio (`implements`) |
| `repository` | `UsuarioRepository`, `AvionRepository`, `AeropuertoRepository`, `VueloRepository`, `ReservaRepository` | Acceso a MongoDB (`extends MongoRepository`) |
| `model` | Clases de la [Vista Lógica](04-vista-logica-clases.md) | Documentos de MongoDB |
| `notification` | `Notificador`, `EmailNotificador` | Envío de correos |
| `config` | `SecurityConfig`, `MongoConfig`, `DataSeeder` | Seguridad, persistencia y datos de prueba |
| `exception` | `GlobalExceptionHandler`, `RecursoNoEncontradoException`, `ReglaNegocioException`, `ConflictoException` | Errores en JSON con su código HTTP |

## Relaciones del diagrama

| Relación | Tipo UML | Ejemplo |
|---|---|---|
| Controller → interfaz de su Service | Dependencia | `ReservaController` → `IReservaService` |
| ServiceImpl → su interfaz | Realización | `ReservaServiceImpl` → `IReservaService` |
| ServiceImpl → Repository | Dependencia | `ReservaServiceImpl` → `ReservaRepository`, `UsuarioRepository` |
| Repository → `MongoRepository<T, ID>` | Generalización | `VueloRepository extends MongoRepository<Vuelo, String>` |
| Repository → clase del modelo | Dependencia | `VueloRepository` → `Vuelo` |
| ServiceImpl → `Notificador` | Dependencia | `VueloServiceImpl`, `ReservaServiceImpl` → `Notificador` |
| `EmailNotificador` → `Notificador` | Realización | `EmailNotificador implements Notificador` |

## Por qué los controladores dependen de interfaces

Los controladores conocen `IReservaService`, no `ReservaServiceImpl`. Spring inyecta la
implementación al arrancar (inversión de dependencias). Esto permite:

- **Bajo acoplamiento:** cambiar la implementación no obliga a tocar el controlador.
- **Pruebas sin base de datos:** en `ReservaControllerTest` el servicio se reemplaza por un
  simulacro (`@MockBean`) y se prueba solo la capa HTTP.
