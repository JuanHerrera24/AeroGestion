# Vista 3: Lógica (Diagrama de Clases)

**Objetivo:** modelo del dominio completo con **abstracción y herencia** (`extends`),
**interfaces** (`implements`), atributos, métodos, enumeraciones y relaciones con
multiplicidad.

## Abstracción y herencia (`extends`)

| Clase abstracta | Subclases | Por qué |
|---|---|---|
| `EntidadBase` | `Usuario`, `Avion`, `Aeropuerto`, `Vuelo`, `Reserva` | Todas comparten `id` y fechas de auditoría |
| `Usuario` | `Administrador`, `Pasajero` | Datos comunes de login; cada rol define sus permisos (`puedeGestionarVuelos()` abstracto) |
| `Asiento` | `AsientoEconomico`, `AsientoEjecutivo`, `AsientoPrimeraClase` | Cada clase define su factor de precio (`getFactorPrecio()` abstracto): polimorfismo |

## Interfaces (`implements`): contratos

| Interfaz | Métodos | La implementan |
|---|---|---|
| `Tarificable` | `calcularPrecio(tarifaBase): double` | `Asiento` (y por herencia sus 3 subclases) |
| `Cancelable` | `cancelar(): void`, `puedeCancelarse(): boolean` | `Vuelo`, `Reserva` |
| `Notificador` | `enviarConfirmacion(Reserva)`, `enviarCancelacion(Reserva)` | `EmailNotificador` |

## Enumeraciones

| Enum | Valores |
|---|---|
| `Rol` | ADMIN, PASAJERO |
| `EstadoAvion` | ACTIVO, EN_MANTENIMIENTO, FUERA_DE_SERVICIO |
| `EstadoVuelo` | PROGRAMADO, RETRASADO, EMBARCANDO, EN_VUELO, ATERRIZADO, CANCELADO |
| `EstadoReserva` | PENDIENTE, CONFIRMADA, CANCELADA |

## Relaciones

| Relación | Tipo | Multiplicidad | Significado |
|---|---|---|---|
| Avion ◆── Asiento | **Composición** | 1 → 1..* | Los asientos no existen sin su avión |
| Vuelo ──> Avion | Asociación | * → 1 | Un vuelo lo opera un avión |
| Vuelo ──> Aeropuerto (origen) | Asociación | * → 1 | |
| Vuelo ──> Aeropuerto (destino) | Asociación | * → 1 | |
| Reserva ──> Pasajero | Asociación | * → 1 | Un pasajero tiene muchas reservas |
| Reserva ──> Vuelo | Asociación | * → 1 | Un vuelo tiene muchas reservas |
| Reserva ◇── Asiento | **Agregación** | 1 → 1..* | Asientos elegidos en la reserva |
| EmailNotificador ┄> Reserva | Dependencia | | Usa la reserva para armar el correo |

