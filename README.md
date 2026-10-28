# ✈️ AeroGestión

Sistema para la gestión de una aerolínea: permite administrar vuelos y la flota de aviones,
y que los pasajeros busquen y reserven vuelos con selección de asientos.

Se expone como una **API REST** que se prueba con Postman.

**Stack:** Java 21 · Spring Boot 3.3 · Spring Security · Spring Data MongoDB · MongoDB 8

## ▶️ Cómo ejecutarlo

**Opción fácil:** doble clic en **`INICIAR.bat`**. Levanta MongoDB y la API en
`http://localhost:8080/api`.

**Opción manual:**
```bash
cd backend
mvn spring-boot:run
```
Requisitos: Java 21, Maven y MongoDB corriendo en `localhost:27017`.

La primera vez se cargan **datos de demostración**: 6 aeropuertos, 5 aviones y 100 vuelos
para los 10 días siguientes. Los vuelos se llaman `AG1XX-DD`, donde `DD` es el día contado
desde la carga (`AG101-01` sale al día siguiente).

## 🧪 Probar con Postman

1. Abre Postman → **Import** → elige `postman/AeroGestion.postman_collection.json`.
2. La colección trae 4 carpetas: **Público**, **Pasajero**, **Administrador** y
   **Validaciones y seguridad**. La autenticación ya está configurada en cada carpeta.
3. Si hace falta, ajusta las variables de la colección `vuelo` y `fecha`.

### Cuentas de prueba (HTTP Basic: email y contraseña)

| Rol | Email | Contraseña |
|---|---|---|
| Administrador | admin@aerogestion.com | Admin2026! |
| Pasajero | pasajero@aerogestion.com | Pasajero2026! |

## 📡 Endpoints

### Públicos (sin iniciar sesión)
| Método | Ruta | Descripción | HU |
|---|---|---|---|
| POST | `/api/auth/registro` | Registrar pasajero | HU-01 |
| GET | `/api/aeropuertos` | Listar aeropuertos | |
| GET | `/api/vuelos/buscar?origen=LIM&destino=CUZ&fecha=2026-11-01&orden=precio` | Buscar vuelos | HU-09 |
| GET | `/api/vuelos/proximos` | Próximas salidas | |
| GET | `/api/vuelos/{codigo}` | Detalle y precio por clase | HU-10 |
| GET | `/api/vuelos/{codigo}/asientos` | Mapa de asientos | HU-11 |

### Pasajero
| Método | Ruta | Descripción | HU |
|---|---|---|---|
| GET | `/api/auth/yo` | Usuario autenticado | HU-02 |
| POST | `/api/reservas` | Reservar asientos | HU-12 |
| GET | `/api/reservas` | Mis reservas | HU-13 |
| GET | `/api/reservas/{codigo}` | Detalle de una reserva | HU-13 |
| PATCH | `/api/reservas/{codigo}/cancelar` | Cancelar (hasta 24 h antes) | HU-14 |

### Administrador
| Método | Ruta | Descripción | HU |
|---|---|---|---|
| GET | `/api/admin/resumen` | Indicadores del panel | HU-08 |
| GET | `/api/admin/aviones` | Listar flota | HU-03 |
| POST | `/api/admin/aviones` | Registrar avión | HU-03 |
| PATCH | `/api/admin/aviones/{matricula}/estado` | Cambiar estado del avión | HU-04 |
| PUT | `/api/admin/aviones/{matricula}/asientos` | Configurar mapa de asientos | HU-05 |
| GET | `/api/admin/vuelos?estado=&desde=&hasta=` | Listar vuelos con filtros | HU-08 |
| POST | `/api/admin/vuelos` | Programar vuelo | HU-06 |
| PUT | `/api/admin/vuelos/{codigo}` | Editar vuelo | HU-07 |
| PATCH | `/api/admin/vuelos/{codigo}/estado` | Cambiar estado (RETRASADO, EN_VUELO…) | HU-07 |
| PATCH | `/api/admin/vuelos/{codigo}/cancelar` | Cancelar vuelo y sus reservas | HU-07 |

### Códigos de respuesta
| Código | Significa |
|---|---|
| 200 / 201 | Correcto / creado |
| 400 | Datos inválidos o regla de negocio incumplida (el JSON dice por qué) |
| 401 | Falta iniciar sesión o credenciales incorrectas |
| 403 | El rol no tiene permiso |
| 404 | No existe el vuelo, avión o reserva |

## 📏 Reglas de negocio

- Origen y destino distintos; la llegada debe ser posterior a la salida.
- Un avión no puede tener dos vuelos a la vez ni volar si no está `ACTIVO`.
- Precio según la clase del asiento: Económica ×1.0, Ejecutiva ×1.8, Primera ×3.0.
- Dos pasajeros no pueden reservar el mismo asiento (operación atómica en MongoDB).
- Una reserva se puede cancelar hasta 24 horas antes de la salida.
- Si se cancela un vuelo, se cancelan sus reservas y se avisa a los pasajeros por correo.

## 📦 Entregables

| Entregable | Archivo |
|---|---|
| Historias de usuario (formato GitHub) | [docs/01-historias-de-usuario.md](docs/01-historias-de-usuario.md) |
| Vista 1: Contexto | [docs/02-vista-contexto.md](docs/02-vista-contexto.md) · [imagen](diagramas/Vista%20de%20Contexto.png) |
| Vista 2: Funcional (componentes) | [docs/03-vista-funcional-componentes.md](docs/03-vista-funcional-componentes.md) · [imagen](diagramas/Vista%20funcional.png) |
| Vista 3: Lógica (clases) | [docs/04-vista-logica-clases.md](docs/04-vista-logica-clases.md) |
| Proyecto de Visual Paradigm | [diagramas/AeroGestion.vpp](diagramas/AeroGestion.vpp) |
| Colección de Postman | [postman/AeroGestion.postman_collection.json](postman/AeroGestion.postman_collection.json) |

## 🗂️ Estructura del código

```
backend/src/main/java/com/aerogestion/
├── AeroGestionApplication.java
├── controller/          AuthController, VueloController, ReservaController
│   └── admin/           AdminController, AdminAvionController, AdminVueloController
├── dto/                 Objetos JSON de entrada y salida
├── service/             Interfaces IAuthService, IAvionService, IVueloService, IBusquedaService, IReservaService
│   └── impl/            Implementaciones con las reglas de negocio
├── repository/          Repositorios Spring Data MongoDB
├── model/               EntidadBase, Usuario, Pasajero, Administrador, Avion, Asiento (+3 clases),
│                        Aeropuerto, Vuelo, Reserva, interfaces Tarificable y Cancelable, enums
├── notification/        Notificador, EmailNotificador
├── config/              SecurityConfig, MongoConfig, DataSeeder
└── exception/           Manejo de errores en JSON
```

## ✉️ Correo

Sin una cuenta SMTP configurada, los correos se **simulan** y aparecen en la consola
(`[Correo simulado] ...`). Para enviarlos de verdad, completa `spring.mail.username` y
`spring.mail.password` en `application.properties`.
