# ✈️ AeroGestión

Sistema para la gestión de una aerolínea: permite administrar vuelos y la flota de aviones,
y que los pasajeros busquen y reserven vuelos con selección de asientos.

**Stack (100 % Java):** Java 21 · Spring Boot 3.3 · Spring Security · Thymeleaf · MongoDB 8

## ▶️ Cómo ejecutarlo

**Opción fácil:** doble clic en **`INICIAR.bat`**. Levanta MongoDB, arranca el sistema y abre
http://localhost:8080 en el navegador.

**Opción manual:**
```bash
cd backend
mvn spring-boot:run
```
Requisitos: Java 21, Maven y MongoDB corriendo en `localhost:27017`.

La primera vez se cargan **datos de demostración**: 6 aeropuertos, 5 aviones y 100 vuelos
para los próximos 10 días.

### Cuentas de prueba

| Rol | Email | Contraseña |
|---|---|---|
| Administrador | admin@aerogestion.com | Admin2026! |
| Pasajero | pasajero@aerogestion.com | Pasajero2026! |

## Funcionalidades (historias de usuario)

| Rol | Puede |
|---|---|
| Visitante | Buscar vuelos por origen, destino y fecha; ver detalle y precios por clase; registrarse |
| Pasajero | Elegir asientos en el mapa del avión, reservar, ver sus reservas y cancelarlas (hasta 24 h antes) |
| Administrador | Panel con indicadores; registrar aviones y su distribución de asientos; cambiar su estado; programar, editar, retrasar y cancelar vuelos |

**Reglas de negocio:** origen ≠ destino, llegada después de la salida, un avión no puede tener
dos vuelos a la vez ni volar si no está activo, el precio depende de la clase
(Económica ×1.0, Ejecutiva ×1.8, Primera ×3.0), dos pasajeros no pueden reservar el mismo
asiento (operación atómica en MongoDB) y al cancelar un vuelo se cancelan sus reservas
y se avisa por correo.

## Entregables

| Entregable | Archivo |
|---|---|
| Historias de usuario (formato GitHub) | [docs/01-historias-de-usuario.md](docs/01-historias-de-usuario.md) |
| Vista 1: Contexto | [docs/02-vista-contexto.md](docs/02-vista-contexto.md) · [imagen](diagramas/Vista%20de%20Contexto.png) |
| Vista 2: Funcional (componentes) | [docs/03-vista-funcional-componentes.md](docs/03-vista-funcional-componentes.md) · [imagen](diagramas/Vista%20funcional.png) |
| Vista 3: Lógica (clases) | [docs/04-vista-logica-clases.md](docs/04-vista-logica-clases.md) · [imagen](diagramas/Vista%20logica.png) |

## Estructura del código

```
backend/src/main/java/com/aerogestion/
├── AeroGestionApplication.java
├── controller/          HomeController, BusquedaController, ReservaController
│   └── admin/           AdminController, AdminAvionController, AdminVueloController
├── dto/                 Formularios y datos para las vistas
├── service/             Interfaces IAuthService, IAvionService, IVueloService, IBusquedaService, IReservaService
│   └── impl/            Implementaciones
├── repository/          Repositorios Spring Data MongoDB
├── model/               EntidadBase, Usuario, Pasajero, Administrador, Avion, Asiento (+3 clases),
│                        Aeropuerto, Vuelo, Reserva, interfaces Tarificable y Cancelable, enums
├── notification/        Notificador, EmailNotificador
├── config/              SecurityConfig, MongoConfig, WebConfig, DataSeeder
└── exception/           Manejo de errores
backend/src/main/resources/
├── templates/           Vistas Thymeleaf (inicio, búsqueda, asientos, reservas, admin)
├── static/css/          Estilos
└── application.properties
```

## Correo

Sin una cuenta SMTP configurada, los correos se **simulan** y aparecen en la consola
(`[Correo simulado] ...`). Para enviarlos de verdad, completa `spring.mail.username` y
`spring.mail.password` en `application.properties` (con una contraseña de aplicación de Gmail).
