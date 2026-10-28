# AeroGestión: Historias de Usuario

> Formato: **GitHub Issues**. Cada historia se puede copiar tal cual como un *issue*
> usando la plantilla `.github/ISSUE_TEMPLATE/historia-de-usuario.md`.
>
> **Notación:** *Como* [rol] *quiero* [acción] *para* [beneficio]
> **Criterios de aceptación:** formato Gherkin (*Dado / Cuando / Entonces*)
> **Prioridad:** MoSCoW (Must / Should / Could) · **Estimación:** Story Points (Fibonacci)

## Roles

| Rol | Descripción |
|---|---|
| **Visitante** | Persona no registrada que consulta vuelos |
| **Pasajero** | Usuario registrado que reserva vuelos y elige asientos |
| **Administrador** | Empleado de la aerolínea que gestiona flota y vuelos |

## Épicas

| Épica | Historias |
|---|---|
| E1: Autenticación y cuentas | HU-01, HU-02 |
| E2: Gestión de flota | HU-03, HU-04, HU-05 |
| E3: Gestión de vuelos | HU-06, HU-07, HU-08 |
| E4: Búsqueda de vuelos | HU-09, HU-10 |
| E5: Reservas y asientos | HU-11, HU-12, HU-13, HU-14 |

---

## E1: Autenticación y cuentas

### HU-01 · Registro de pasajero
**Labels:** `historia-de-usuario` `epica:autenticacion` `prioridad:must`
**Estimación:** 3 SP

**Como** visitante
**quiero** registrarme con mis datos personales
**para** poder reservar vuelos con mi propia cuenta.

#### Criterios de aceptación
- [ ] **Dado** que estoy en el formulario de registro, **cuando** ingreso nombre, apellido, email, documento, teléfono, fecha de nacimiento y contraseña válidos, **entonces** se crea mi cuenta con rol `PASAJERO`.
- [ ] **Dado** que el email ya está registrado, **cuando** intento registrarme, **entonces** el sistema muestra *"El email ya está en uso"* y no crea la cuenta.
- [ ] **Dado** que la contraseña tiene menos de 8 caracteres, **cuando** envío el formulario, **entonces** se rechaza el registro.
- [ ] La contraseña se guarda cifrada (BCrypt), nunca en texto plano.

**Notas técnicas:** `POST /api/auth/registro` · colección `usuarios`

---

### HU-02 · Inicio de sesión
**Labels:** `historia-de-usuario` `epica:autenticacion` `prioridad:must`
**Estimación:** 3 SP

**Como** pasajero o administrador
**quiero** iniciar sesión con mi email y contraseña
**para** acceder a las funciones de mi rol.

#### Criterios de aceptación
- [ ] **Dado** que mis credenciales son correctas, **cuando** inicio sesión, **entonces** el sistema me identifica y me da acceso a las funciones de mi rol.
- [ ] **Dado** que mis credenciales son incorrectas, **cuando** inicio sesión, **entonces** veo *"Credenciales inválidas"*.
- [ ] **Dado** que soy pasajero, **cuando** intento entrar a una ruta de administrador, **entonces** recibo un error 403.

**Notas técnicas:** `GET /api/auth/yo` · autenticación HTTP Basic (Spring Security)

---

## E2: Gestión de flota

### HU-03 · Registrar avión
**Labels:** `historia-de-usuario` `epica:flota` `prioridad:must`
**Estimación:** 5 SP

**Como** administrador
**quiero** registrar un avión con su matrícula, modelo, fabricante y año
**para** tenerlo disponible al programar vuelos.

#### Criterios de aceptación
- [ ] **Dado** que ingreso datos válidos, **cuando** guardo el avión, **entonces** queda registrado con estado `ACTIVO`.
- [ ] **Dado** que la matrícula ya existe, **cuando** intento guardarlo, **entonces** el sistema rechaza el registro.
- [ ] **Dado** que hay aviones registrados, **cuando** abro la sección *Flota*, **entonces** veo la lista con matrícula, modelo, capacidad y estado.

**Notas técnicas:** `GET /api/admin/aviones`, `POST /api/admin/aviones` · colección `aviones`

---

### HU-04 · Actualizar estado de un avión
**Labels:** `historia-de-usuario` `epica:flota` `prioridad:should`
**Estimación:** 3 SP

**Como** administrador
**quiero** cambiar el estado de un avión (activo, en mantenimiento, fuera de servicio)
**para** evitar que se asigne a vuelos un avión no disponible.

#### Criterios de aceptación
- [ ] **Dado** un avión `ACTIVO`, **cuando** lo marco `EN_MANTENIMIENTO`, **entonces** ya no aparece como opción al programar vuelos.
- [ ] **Dado** un avión con vuelos programados a futuro, **cuando** intento ponerlo `FUERA_DE_SERVICIO`, **entonces** el sistema me advierte qué vuelos están afectados.

**Notas técnicas:** `PATCH /api/admin/aviones/{matricula}/estado`

---

### HU-05 · Configurar mapa de asientos
**Labels:** `historia-de-usuario` `epica:flota` `prioridad:must`
**Estimación:** 5 SP

**Como** administrador
**quiero** definir la distribución de asientos de un avión por clase (Primera, Ejecutiva, Económica)
**para** que los pasajeros puedan elegir asiento al reservar.

#### Criterios de aceptación
- [ ] **Dado** un avión registrado, **cuando** indico filas y letras para cada clase, **entonces** se generan los asientos (ej. `1A`, `12C`).
- [ ] La capacidad total del avión se calcula automáticamente a partir de los asientos.
- [ ] Cada clase tiene un factor de precio: Económica ×1.0, Ejecutiva ×1.8, Primera ×3.0.

**Notas técnicas:** `PUT /api/admin/aviones/{matricula}/asientos` · asientos embebidos en el documento del avión

---

## E3: Gestión de vuelos

### HU-06 · Programar vuelo
**Labels:** `historia-de-usuario` `epica:vuelos` `prioridad:must`
**Estimación:** 5 SP

**Como** administrador
**quiero** programar un vuelo indicando código, origen, destino, fecha/hora de salida y llegada, avión y tarifa base
**para** que los pasajeros puedan encontrarlo y reservarlo.

#### Criterios de aceptación
- [ ] **Dado** que ingreso datos válidos, **cuando** guardo el vuelo, **entonces** queda en estado `PROGRAMADO`.
- [ ] **Dado** que el origen y el destino son iguales, **cuando** guardo, **entonces** se rechaza.
- [ ] **Dado** que la llegada es anterior a la salida, **cuando** guardo, **entonces** se rechaza.
- [ ] **Dado** que el avión ya tiene otro vuelo en ese horario o no está `ACTIVO`, **cuando** lo asigno, **entonces** el sistema no lo permite.

**Notas técnicas:** `POST /api/admin/vuelos` · colección `vuelos`

---

### HU-07 · Modificar o cancelar vuelo
**Labels:** `historia-de-usuario` `epica:vuelos` `prioridad:should`
**Estimación:** 5 SP

**Como** administrador
**quiero** modificar el horario o el estado de un vuelo, o cancelarlo
**para** reflejar retrasos y cambios operativos.

#### Criterios de aceptación
- [ ] **Dado** un vuelo `PROGRAMADO`, **cuando** cambio su estado a `RETRASADO` con una nueva hora, **entonces** se actualiza para todos los pasajeros.
- [ ] **Dado** un vuelo con reservas, **cuando** lo cancelo, **entonces** sus reservas pasan a `CANCELADA` y se notifica a los pasajeros por correo.
- [ ] No se puede cancelar un vuelo en estado `EN_VUELO` o `ATERRIZADO`.

**Notas técnicas:** `PUT /api/admin/vuelos/{codigo}`, `PATCH /api/admin/vuelos/{codigo}/estado`, `PATCH /api/admin/vuelos/{codigo}/cancelar`

---

### HU-08 · Ver panel de vuelos y ocupación
**Labels:** `historia-de-usuario` `epica:vuelos` `prioridad:could`
**Estimación:** 3 SP

**Como** administrador
**quiero** ver la lista de vuelos con su porcentaje de ocupación
**para** tomar decisiones operativas.

#### Criterios de aceptación
- [ ] **Dado** que hay vuelos, **cuando** abro el panel, **entonces** veo código, ruta, fecha, estado y ocupación (asientos ocupados / capacidad).
- [ ] Puedo filtrar por estado y por rango de fechas.

**Notas técnicas:** `GET /api/admin/resumen`, `GET /api/admin/vuelos?estado=&desde=&hasta=`

---

## E4: Búsqueda de vuelos

### HU-09 · Buscar vuelos
**Labels:** `historia-de-usuario` `epica:busqueda` `prioridad:must`
**Estimación:** 5 SP

**Como** visitante o pasajero
**quiero** buscar vuelos por ciudad de origen, destino y fecha
**para** encontrar opciones que se ajusten a mi viaje.

#### Criterios de aceptación
- [ ] **Dado** que existen vuelos para esa ruta y fecha, **cuando** busco, **entonces** veo hora de salida y llegada, duración, precio desde (económica) y asientos disponibles.
- [ ] **Dado** que no hay vuelos, **cuando** busco, **entonces** veo *"No hay vuelos disponibles para esta búsqueda"*.
- [ ] Solo se muestran vuelos `PROGRAMADO` o `RETRASADO` con fecha futura.
- [ ] Puedo ordenar los resultados por precio u hora de salida.

**Notas técnicas:** `GET /api/vuelos/buscar?origen=LIM&destino=CUZ&fecha=2026-11-15` (público, sin login)

---

### HU-10 · Ver detalle de un vuelo
**Labels:** `historia-de-usuario` `epica:busqueda` `prioridad:should`
**Estimación:** 2 SP

**Como** visitante o pasajero
**quiero** ver el detalle de un vuelo
**para** conocer el avión, la duración y los precios por clase antes de reservar.

#### Criterios de aceptación
- [ ] **Dado** un resultado de búsqueda, **cuando** lo selecciono, **entonces** veo aeropuertos, horarios, duración, modelo de avión y precio por clase.

**Notas técnicas:** `GET /api/vuelos/{codigo}`

---

## E5: Reservas y asientos

### HU-11 · Seleccionar asiento
**Labels:** `historia-de-usuario` `epica:reservas` `prioridad:must`
**Estimación:** 8 SP

**Como** pasajero
**quiero** ver el mapa de asientos del vuelo y elegir el mío
**para** viajar en el lugar y la clase que prefiero.

#### Criterios de aceptación
- [ ] **Dado** un vuelo, **cuando** abro el mapa, **entonces** veo cada asiento identificado como disponible u ocupado y por clase.
- [ ] **Dado** un asiento ocupado, **cuando** intento seleccionarlo, **entonces** el sistema no lo permite.
- [ ] **Dado** que selecciono un asiento, **entonces** veo su precio (tarifa base × factor de la clase).
- [ ] **Dado** que dos pasajeros eligen el mismo asiento a la vez, **cuando** confirman, **entonces** solo el primero lo obtiene.

**Notas técnicas:** `GET /api/vuelos/{codigo}/asientos`

---

### HU-12 · Confirmar reserva
**Labels:** `historia-de-usuario` `epica:reservas` `prioridad:must`
**Estimación:** 5 SP

**Como** pasajero
**quiero** confirmar la reserva de los asientos elegidos
**para** asegurar mi lugar en el vuelo.

#### Criterios de aceptación
- [ ] **Dado** que elegí uno o más asientos disponibles, **cuando** confirmo, **entonces** se crea una reserva `CONFIRMADA` con un código único (ej. `AG-7K2P9X`) y el total calculado.
- [ ] Los asientos reservados quedan ocupados en el vuelo.
- [ ] Recibo un correo de confirmación con el código de reserva.
- [ ] **Dado** que no inicié sesión, **cuando** intento confirmar, **entonces** se me pide iniciar sesión.

**Notas técnicas:** `POST /api/reservas` · colección `reservas`

---

### HU-13 · Consultar mis reservas
**Labels:** `historia-de-usuario` `epica:reservas` `prioridad:should`
**Estimación:** 3 SP

**Como** pasajero
**quiero** ver el historial de mis reservas
**para** revisar mis próximos vuelos y los anteriores.

#### Criterios de aceptación
- [ ] **Dado** que tengo reservas, **cuando** abro *Mis reservas*, **entonces** veo código, vuelo, ruta, fecha, asientos, total y estado.
- [ ] Solo veo mis propias reservas, no las de otros pasajeros.

**Notas técnicas:** `GET /api/reservas`

---

### HU-14 · Cancelar reserva
**Labels:** `historia-de-usuario` `epica:reservas` `prioridad:should`
**Estimación:** 3 SP

**Como** pasajero
**quiero** cancelar una reserva
**para** liberar el asiento si ya no voy a viajar.

#### Criterios de aceptación
- [ ] **Dado** que faltan más de 24 horas para el vuelo, **cuando** cancelo, **entonces** la reserva pasa a `CANCELADA` y los asientos vuelven a estar disponibles.
- [ ] **Dado** que faltan menos de 24 horas, **cuando** intento cancelar, **entonces** el sistema no lo permite.
- [ ] Recibo un correo confirmando la cancelación.

**Notas técnicas:** `PATCH /api/reservas/{codigo}/cancelar`

---

## Resumen del backlog

| ID | Historia | Rol | Prioridad | SP |
|---|---|---|---|---|
| HU-01 | Registro de pasajero | Visitante | Must | 3 |
| HU-02 | Inicio de sesión | Pasajero/Admin | Must | 3 |
| HU-03 | Registrar avión | Admin | Must | 5 |
| HU-04 | Actualizar estado de avión | Admin | Should | 3 |
| HU-05 | Configurar mapa de asientos | Admin | Must | 5 |
| HU-06 | Programar vuelo | Admin | Must | 5 |
| HU-07 | Modificar/cancelar vuelo | Admin | Should | 5 |
| HU-08 | Panel de vuelos y ocupación | Admin | Could | 3 |
| HU-09 | Buscar vuelos | Visitante/Pasajero | Must | 5 |
| HU-10 | Ver detalle de vuelo | Visitante/Pasajero | Should | 2 |
| HU-11 | Seleccionar asiento | Pasajero | Must | 8 |
| HU-12 | Confirmar reserva | Pasajero | Must | 5 |
| HU-13 | Consultar mis reservas | Pasajero | Should | 3 |
| HU-14 | Cancelar reserva | Pasajero | Should | 3 |
| | **Total** | | | **58** |
