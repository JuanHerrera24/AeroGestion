# Vista 1: Contexto

**Objetivo:** mostrar a **AeroGestión** como una sola caja negra y a las **entidades externas**
con las que intercambia información. No muestra cómo funciona por dentro, solo **qué entra y
qué sale** del sistema.

Se representa con un **diagrama de contexto (DFD nivel 0, notación Yourdon)**:

- El **círculo** del centro es el sistema completo (*proceso 0*).
- Los **rectángulos** son las **entidades externas**: personas o sistemas que están fuera de
  AeroGestión pero le envían o reciben datos.
- Las **flechas** son los **flujos de datos**. Cada una dice qué información viaja y qué
  historias de usuario la originan.

## Entidades externas

| Entidad externa | Tipo | Descripción |
|---|---|---|
| **Visitante** | Persona | No registrado; busca vuelos, ve su detalle y puede registrarse |
| **Pasajero** | Persona | Registrado; inicia sesión, elige asientos, reserva y cancela |
| **Administrador de la Aerolínea** | Persona | Empleado; gestiona la flota, los asientos y los vuelos |
| **Servicio de Correo (SMTP)** | Sistema externo | Envía los correos de confirmación y cancelación |

MongoDB **no** aparece como entidad externa: en un diagrama de contexto los almacenes de datos
son parte del sistema (se verán por dentro en los diagramas de nivel 1).

## Flujos de datos

| Desde → Hacia | Flujo de datos | Historias |
|---|---|---|
| Visitante → AeroGestión | Busca vuelos, consulta detalle y se registra | HU-01, HU-09, HU-10, HU-20, HU-21, HU-22 |
| AeroGestión → Visitante | Resultados de búsqueda y precios por clase | HU-09, HU-10 |
| Pasajero → AeroGestión | Inicia sesión, elige asientos, reserva y cancela | HU-02, HU-11, HU-12, HU-14, HU-15, HU-23 |
| AeroGestión → Pasajero | Mapa de asientos, código de reserva e historial | HU-11, HU-12, HU-13 |
| Administrador → AeroGestión | Gestiona flota, asientos y vuelos | HU-03 a HU-07, HU-16 a HU-19 |
| AeroGestión → Administrador | Panel de vuelos y ocupación | HU-08 |
| AeroGestión → Servicio de Correo | Solicita envío de confirmación o cancelación | HU-07, HU-12, HU-14, HU-24 |
| Servicio de Correo → AeroGestión | Confirma el envío del correo | HU-07, HU-12, HU-14, HU-24 |

## Diagrama

![VistaContexto](../diagramas/Vista%20de%20Contexto.png)
