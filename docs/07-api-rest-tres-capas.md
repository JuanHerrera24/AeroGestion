# De la Web a una API REST: AeroGestión con Spring Boot y MongoDB

Este documento aplica la guía de clase **«De la Web a una API REST con Spring Boot y MongoDB»**
al caso AeroGestión. Responde la pregunta guía:

> ¿Cómo convertimos un modelo de clases en una API que otros programas puedan utilizar?

| Concepto de la guía (caso Editorial) | En AeroGestión |
|---|---|
| `Publicacion` (abstracta) → `Libro`, `Disco` | `Usuario` (abstracta) → `Pasajero`, `Administrador` · `Asiento` (abstracta) → `AsientoEconomico`, `AsientoEjecutivo`, `AsientoPrimeraClase` |
| Colección `publicaciones` con discriminador `tipo` | Colección `usuarios` con `tipo: PASAJERO / ADMINISTRADOR` · asientos embebidos con `tipo: ECONOMICO / EJECUTIVO / PRIMERA_CLASE` |
| `LibroController → LibroService → PublicacionRepository` | `ReservaController → IReservaService → ReservaRepository` (y las demás) |
| Cliente: navegador o Postman | Postman (`postman/AeroGestion.postman_collection.json`) |

---

## 1. Una solicitud viaja y una respuesta regresa

```
Postman ──(solicitud HTTP)──▶ API Spring Boot ──▶ MongoDB
        ◀─(respuesta HTTP)───                 ◀──
```

**¿Por qué el cliente no se conecta directamente a MongoDB?** Porque la base de datos solo
guarda; no sabe que un asiento ocupado no se puede volver a reservar ni que solo un
administrador puede programar vuelos. La API es la única puerta de entrada: autentica,
valida las reglas de negocio y recién entonces persiste. Además, la API puede cambiar de
base de datos sin que los clientes se enteren.

## 2. Recursos y operaciones (rutas que nombran recursos, métodos que expresan la operación)

| Recurso | Método y ruta | Operación | Respuesta exitosa |
|---|---|---|---|
| Vuelos | `GET /api/vuelos/buscar?origen&destino&fecha` | Buscar | 200 + lista (puede estar vacía) |
| Vuelo | `GET /api/vuelos/{codigo}` | Consultar uno | 200 · 404 si no existe |
| Asientos de un vuelo | `GET /api/vuelos/{codigo}/asientos` | Consultar el mapa | 200 |
| Pasajero | `POST /api/auth/registro` | Crear | **201 Created** |
| Reservas | `POST /api/reservas` | Crear | **201 Created** |
| Reservas | `GET /api/reservas` | Listar las mías | 200 |
| Reserva | `PATCH /api/reservas/{codigo}/cancelar` | Modificar parcialmente (estado) | 200 |
| Aviones | `POST /api/admin/aviones` | Crear | **201 Created** |
| Asientos de un avión | `PUT /api/admin/aviones/{matricula}/asientos` | Reemplazar la distribución | 200 |
| Vuelos | `POST /api/admin/vuelos` | Programar | **201 Created** |
| Vuelo | `PUT /api/admin/vuelos/{codigo}` | Reemplazar datos | 200 |
| Vuelo | `PATCH /api/admin/vuelos/{codigo}/estado` | Modificar parcialmente | 200 |

La lista completa está en el [README](../README.md#endpoints).

## 3. Códigos HTTP: ¿qué ocurrió?

| Código | Cuándo lo devuelve AeroGestión | Ejemplo |
|---|---|---|
| 200 OK | Consulta u operación exitosa | `GET /api/reservas` |
| 201 Created | Se creó un recurso | `POST /api/reservas` |
| 400 Bad Request | Los datos enviados no son válidos | Reserva sin asientos, origen = destino, JSON mal formado |
| 401 Unauthorized | Falta iniciar sesión o las credenciales son incorrectas | `POST /api/reservas` sin Basic Auth |
| 403 Forbidden | El rol no tiene permiso | Un pasajero entra a `/api/admin/**` |
| 404 Not Found | El recurso no existe | `GET /api/vuelos/XX999` |
| 405 Method Not Allowed | La ruta existe, pero no con ese método | `DELETE /api/vuelos/AG100` |
| 409 Conflict | Datos válidos que chocan con el estado actual | Asiento ya ocupado, matrícula repetida, avión con otro vuelo en ese horario, cancelar a menos de 24 h |
| 500 Internal Server Error | Error inesperado (se registra en el log y no se muestran detalles técnicos) | — |

**400 vs 409.** Un 400 se corrige cambiando la petición (los datos están mal). Un 409 dice
que la petición está bien formada, pero el estado actual lo impide: el mismo `POST` habría
funcionado un minuto antes, cuando el asiento estaba libre.

Todos los errores responden con el mismo formato JSON (`ErrorDTO`):

```json
{
  "status": 409,
  "error": "Conflicto",
  "mensaje": "El asiento 1A ya está ocupado",
  "fecha": "2026-11-18T10:20:41.650644"
}
```

**¿Qué devolvemos al consultar un vuelo que no existe?** `404 Not Found` con un mensaje. Es más
informativo que `200` con un cuerpo vacío: el cliente distingue «no hay datos» de «la consulta
falló» sin tener que interpretar el cuerpo.

## 4. La clase y su representación JSON

La clase `Reserva` (dominio) y el JSON que devuelve la API **no son lo mismo**. El controlador
convierte la entidad en un DTO (`ReservaDTO`) que:

- **omite** datos internos (el `id` de MongoDB, el hash de la contraseña del pasajero);
- **aplana** referencias (`"vuelo": "AG101-03"` en lugar del documento completo del vuelo);
- **agrega** datos calculados (`"cancelable": true`, el precio de cada asiento según su clase).

```http
POST /api/reservas
Authorization: Basic cGFzYWplcm9AYWVyb2dlc3Rpb24uY29tOlBhc2FqZXJvMjAyNiE=
Content-Type: application/json

{ "codigoVuelo": "AG103-03", "asientos": ["8A", "8B"] }
```

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "codigo": "AG-9RBHLP",
  "vuelo": "AG103-03",
  "ruta": "LIM → AQP",
  "salida": "2026-10-31T18:00:00",
  "pasajero": "Carlos Ramírez",
  "asientos": [
    { "numero": "8A", "clase": "ECONOMICA", "precio": 79.0 },
    { "numero": "8B", "clase": "ECONOMICA", "precio": 79.0 }
  ],
  "total": 158.0,
  "estado": "CONFIRMADA",
  "cancelable": true
}
```

## 5. MongoDB: documentos, colecciones y el discriminador `tipo`

Cada colección guarda documentos JSON (BSON). La herencia UML se persiste así:

| Jerarquía | Estrategia de persistencia | Por qué |
|---|---|---|
| `Usuario` → `Pasajero`, `Administrador` | **Una colección** `usuarios` con campo `tipo` | Se buscan juntos por email al iniciar sesión, sin saber de antemano el rol |
| `Asiento` → 3 clases | **Embebidos** en el documento del avión (composición) y copiados en la reserva | Un asiento no existe sin su avión y se lee siempre junto con él |
| `Vuelo` → `Avion`, `Aeropuerto` | **Referencias** (`@DBRef`) | Un avión opera muchos vuelos; se actualiza una sola vez |

Documento real de la colección `usuarios`:

```json
{
  "_id": { "$oid": "6ae285ac4b3ce264c3499a01" },
  "tipo": "PASAJERO",
  "nombre": "Carlos",
  "apellido": "Ramírez",
  "email": "pasajero@aerogestion.com",
  "rol": "PASAJERO",
  "documentoIdentidad": "70123456",
  "telefono": "987654321",
  "fechaNacimiento": { "$date": "1998-05-14T05:00:00Z" },
  "nacionalidad": "Peruana",
  "passwordHash": "$2a$10$…"
}
```

Documento de la colección `aviones` (asientos embebidos):

```json
{
  "_id": { "$oid": "…" },
  "tipo": "AVION",
  "matricula": "OB-2101",
  "estado": "ACTIVO",
  "asientos": [
    { "numero": "1A", "fila": 1, "letra": "A", "tipo": "PRIMERA_CLASE" },
    { "numero": "1C", "fila": 1, "letra": "C", "tipo": "PRIMERA_CLASE" },
    "… (144 asientos: primera, ejecutiva y económica)"
  ]
}
```

**¿Cómo reconoce la aplicación qué documento es un `Pasajero` y cuál un `Administrador`?** Por el
campo discriminador `tipo`. Cada subclase declara su valor con `@TypeAlias("PASAJERO")` y
`MongoConfig` le indica a Spring Data que lo guarde en el campo `tipo` (en vez del `_class`
por defecto, que guardaría el nombre completo de la clase Java). Al leer, Spring Data mira
`tipo` y crea el objeto de la subclase correcta. Lo comprueba `DiscriminadorTipoTest`.

## 6. Tres capas: ¿qué responsabilidad tiene cada una?

```
Cliente HTTP / Postman
        │
Presentación · Controller   AuthController · VueloController · ReservaController · Admin*Controller
        │ usa
Negocio · Service           IAuthService · IVueloService · IBusquedaService · IReservaService · IAvionService
        │ usa
Acceso a datos · Repository UsuarioRepository · VueloRepository · ReservaRepository · AvionRepository · AeropuertoRepository
        │
      MongoDB

Modelo de dominio (aparte, no es una cuarta capa): Usuario, Pasajero, Vuelo, Avion, Asiento, Reserva…
```

| Capa | Pregunta que responde | Ejemplo en AeroGestión |
|---|---|---|
| Controller | ¿Cómo entra y sale la información por HTTP? | `ReservaController` recibe `POST /api/reservas`, valida el formato del JSON (`@Valid`) y responde `201` con un `ReservaDTO` |
| Service | ¿Qué reglas y pasos requiere el caso de uso? | `ReservaServiceImpl` comprueba que el vuelo admita reservas y que los asientos existan y estén libres, calcula el total y envía el correo |
| Repository | ¿Cómo se consulta o almacena la información? | `ReservaRepository.save(reserva)` guarda el documento en la colección `reservas` |

El diagrama completo de paquetes y dependencias es la [Vista de Desarrollo](05-vista-desarrollo.md).

### Tabla de responsabilidades por operación (actividad de cierre)

| Operación | Controller | Service | Repository |
|---|---|---|---|
| Registrar pasajero | `AuthController.registrar` | `AuthServiceImpl.registrar`: email único, contraseña cifrada | `UsuarioRepository.existsByEmail`, `save` |
| Buscar vuelos | `VueloController.buscar` | `BusquedaServiceImpl.buscar`: origen ≠ destino, filtra los abiertos y ordena | `VueloRepository`, `AeropuertoRepository` |
| Ver mapa de asientos | `VueloController.asientos` | `VueloServiceImpl.obtenerMapaAsientos`: precio por clase y ocupado | `VueloRepository.findByCodigoVuelo` |
| Reservar asientos | `ReservaController.crear` | `ReservaServiceImpl.crear`: reglas de reserva, ocupación atómica, total, correo | `ReservaRepository.save` + actualización atómica en `vuelos` |
| Cancelar reserva | `ReservaController.cancelar` | `ReservaServiceImpl.cancelar`: hasta 24 h antes, libera asientos | `ReservaRepository.save` |
| Registrar avión | `AdminAvionController.registrar` | `AvionServiceImpl.crear`: matrícula única, genera asientos por clase | `AvionRepository.save` |
| Programar vuelo | `AdminVueloController.programar` | `VueloServiceImpl.programar`: ruta, horario y avión disponible | `VueloRepository.save` |
| Cancelar vuelo | `AdminVueloController.cancelar` | `VueloServiceImpl.cancelar`: anula sus reservas y avisa por correo | `VueloRepository`, `ReservaRepository` |

### ¿Por qué el Controller no guarda directamente en MongoDB?

1. **Responsabilidades claras (alta cohesión):** el controlador sabe de HTTP (rutas, JSON,
   códigos); si además validara reglas y guardara, sería una «clase Dios» difícil de cambiar.
2. **Las reglas no se duplican:** cancelar un vuelo anula sus reservas. Si cada controlador
   hiciera eso por su cuenta, tarde o temprano uno lo olvidaría. En el servicio está una sola vez.
3. **Bajo acoplamiento:** el controlador depende de la interfaz `IReservaService`, no de
   MongoDB. Cambiar la base de datos no toca la capa de presentación.
4. **Se puede probar por partes:** las reglas se prueban sin servidor web
   (`ReservaServiceImplTest`) y la capa HTTP sin base de datos (`ReservaControllerTest`).

### Reglas de negocio que se validan en el Service

| Entidad | Regla | Si no se cumple |
|---|---|---|
| Reserva | Entre 1 y 9 asientos, y todos deben existir en el avión | 400 |
| Reserva | El vuelo debe estar `PROGRAMADO` o `RETRASADO` y salir en el futuro | 409 |
| Reserva | Un asiento ocupado no se puede volver a reservar (actualización atómica en MongoDB) | 409 |
| Reserva | Se cancela hasta 24 horas antes de la salida | 409 |
| Vuelo | Origen y destino distintos; llegada posterior a la salida; salida futura | 400 |
| Vuelo | El avión debe estar `ACTIVO`, tener asientos y no tener otro vuelo en ese horario | 409 |
| Avión | La matrícula es única | 409 |
| Pasajero | El email es único | 409 |

## 7. Secuencias

### Secuencia 1: reservar asientos (201 / 400 / 409)

```mermaid
sequenceDiagram
    actor C as Cliente (Postman)
    participant RC as ReservaController
    participant RS as ReservaServiceImpl
    participant VS as IVueloService
    participant M as MongoDB
    participant RR as ReservaRepository

    C->>RC: POST /api/reservas {codigoVuelo, asientos}
    alt JSON inválido (sin asientos)
        RC-->>C: 400 Bad Request + campos con error
    else JSON válido
        RC->>RS: crear(codigoVuelo, asientos, email)
        RS->>VS: obtener(codigoVuelo)
        VS-->>RS: Vuelo
        RS->>RS: validar estado del vuelo y asientos
        alt vuelo cerrado o asiento ocupado
            RS-->>RC: ConflictoException
            RC-->>C: 409 Conflict
        else todo correcto
            RS->>M: ocupar asientos (updateFirst atómico)
            M-->>RS: 1 documento modificado
            RS->>RR: save(reserva)
            RR-->>RS: Reserva guardada
            RS-->>RC: Reserva
            RC-->>C: 201 Created + ReservaDTO (JSON)
        end
    end
```

### Secuencia 2: listar vuelos de distintos tipos de asiento

```mermaid
sequenceDiagram
    actor C as Cliente
    participant VC as VueloController
    participant VS as IVueloService
    participant VR as VueloRepository
    participant M as MongoDB

    C->>VC: GET /api/vuelos/AG101-03/asientos
    VC->>VS: obtenerMapaAsientos("AG101-03")
    VS->>VR: findByCodigoVuelo("AG101-03")
    VR->>M: consultar colección vuelos (+ avión referenciado)
    M-->>VR: documentos con asientos tipo PRIMERA_CLASE / EJECUTIVO / ECONOMICO
    VR-->>VS: Vuelo con su lista de Asiento (cada uno de su subclase)
    VS-->>VC: filas con clase, precio y ocupado
    VC-->>C: 200 OK + JSON
```

Cada asiento llega como su subclase gracias al campo `tipo`, así que `asiento.calcularPrecio()`
aplica el factor correcto (polimorfismo) sin preguntar de qué clase es.

### Secuencia 3: consultar un recurso inexistente

```mermaid
sequenceDiagram
    actor C as Cliente
    participant VC as VueloController
    participant VS as IVueloService
    participant VR as VueloRepository
    participant H as GlobalExceptionHandler

    C->>VC: GET /api/vuelos/XX999
    VC->>VS: obtener("XX999")
    VS->>VR: findByCodigoVuelo("XX999")
    VR-->>VS: Optional.empty()
    VS-->>VC: RecursoNoEncontradoException
    VC->>H: la excepción sube al manejador global
    H-->>C: 404 Not Found + {"mensaje": "No existe el vuelo XX999"}
```

## 8. Reto de la guía: ¿y el DELETE?

En AeroGestión **no se borra** ni un vuelo ni una reserva: se **cancelan** con `PATCH`.

- Cancelar es un **cambio de estado** (`CONFIRMADA → CANCELADA`), no la desaparición del recurso:
  la reserva cancelada sigue en el historial del pasajero (HU-13) y en los indicadores del panel.
- Cancelar un vuelo tiene **efectos de negocio**: anular sus reservas, liberar los asientos y
  avisar a los pasajeros. Un `DELETE` perdería ese rastro.
- Por eso `DELETE /api/vuelos/{codigo}` responde **405 Method Not Allowed**.

Si el sistema necesitara borrar de verdad (por ejemplo, un aeropuerto cargado por error), el flujo sería:

```
DELETE /api/admin/aeropuertos/{codigo}
  → AdminAeropuertoController.eliminar(codigo)
  → AeropuertoService.eliminar(codigo)
       ├─ no existe                        → 404 Not Found
       ├─ hay vuelos que lo usan           → 409 Conflict (no se puede borrar)
       └─ AeropuertoRepository.delete(...) → 204 No Content (sin cuerpo)
```

## 9. Pruebas: peticiones HTTP y respuestas JSON

`backend/src/test` tiene **30 pruebas automáticas** que no necesitan MongoDB:

| Prueba | Capa | Qué comprueba |
|---|---|---|
| `ReservaControllerTest` | Controller | 201, 200, 400 (validación y JSON mal formado), 401, 403, 404 y 409 de `/api/reservas` |
| `VueloControllerTest` | Controller | 200 con lista (también vacía), 404, 400, 405 y 500 con JSON genérico |
| `ReservaServiceImplTest` | Service | Reglas de reserva: total por clase, asiento ocupado o inexistente, vuelo cancelado, reserva simultánea, cancelación con 24 h |
| `DiscriminadorTipoTest` | Persistencia | Que `Pasajero`/`Administrador` y los asientos se guarden y lean por el campo `tipo` |

```bash
cd backend
./mvnw test
```

Para probar a mano, la carpeta **«4. Validaciones, errores y seguridad»** de la colección de
Postman tiene un caso por cada código (400, 401, 403, 404, 405, 409) con una prueba automática
que verifica el código y el formato del error.
