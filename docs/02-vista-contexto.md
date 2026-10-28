# Vista 1: Contexto

**Objetivo:** mostrar **AeroGestión** delimitado por su frontera (System), los **actores**
que interactúan con él desde afuera y los **servicios principales** que ofrece a cada uno.
Se representa con un **diagrama de casos de uso UML**.

## Actores

| Actor | Tipo | Descripción |
|---|---|---|
| **Visitante** | Persona | No registrado; busca y consulta vuelos y puede registrarse |
| **Pasajero** | Persona | Registrado; reserva, elige asiento, consulta y cancela reservas |
| **Administrador** | Persona | Empleado de la aerolínea; gestiona flota y vuelos |
| **MongoDB** | `<<system>>` externo | Base de datos donde se guarda la información |
| **Servicio de correo** | `<<system>>` externo | Envía confirmaciones y cancelaciones de reserva |

## Casos de uso (dentro de AeroGestión)

| Caso de uso | Actores | Historias |
|---|---|---|
| Buscar vuelos | Visitante, Pasajero | HU-09, HU-10 |
| Registrarse | Visitante | HU-01 |
| Iniciar sesión | Pasajero, Administrador | HU-02 |
| Reservar vuelo | Pasajero, Servicio de correo | HU-12, HU-13 |
| Seleccionar asiento | (incluido en Reservar vuelo) | HU-11 |
| Cancelar reserva | Pasajero, Servicio de correo | HU-14 |
| Gestionar flota | Administrador | HU-03, HU-04, HU-05 |
| Gestionar vuelos | Administrador, Servicio de correo | HU-06, HU-07, HU-08 |

Todos los casos de uso guardan y leen su información en **MongoDB**.

## Diagrama

![VistaContexto](../diagramas/Vista%20de%20Contexto.png)

