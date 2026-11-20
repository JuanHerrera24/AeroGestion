# AeroGestión: Requisitos Funcionales

Son 24 requisitos (RF-01 a RF-24). Cada requisito se relaciona con sus [historias de usuario](01-historias-de-usuario.md) y con el
endpoint de la API que lo implementa. Las notas de la [Vista Funcional](03-vista-funcional-componentes.md)
indican qué requisitos cubre cada interfaz.

| ID | Requisito funcional | Historias | Endpoint que lo implementa |
|---|---|---|---|
| RF-01 | El sistema debe permitir registrar pasajeros con sus datos personales y una contraseña cifrada. | HU-01 | `POST /api/auth/registro` |
| RF-02 | El sistema debe autenticar a los usuarios y restringir las funciones según su rol (pasajero o administrador). | HU-02 | `GET /api/auth/yo` · HTTP Basic en toda la API |
| RF-03 | El sistema debe permitir registrar aviones con matrícula única, modelo, fabricante y año. | HU-03 | `POST /api/admin/aviones` |
| RF-04 | El sistema debe permitir cambiar el estado de un avión e informar los vuelos futuros afectados. | HU-04 | `PATCH /api/admin/aviones/{matricula}/estado` |
| RF-05 | El sistema debe permitir configurar la distribución de asientos por clase (Primera, Ejecutiva, Económica). | HU-05 | `PUT /api/admin/aviones/{matricula}/asientos` |
| RF-06 | El sistema debe permitir programar vuelos validando ruta, horario y disponibilidad del avión. | HU-06 | `POST /api/admin/vuelos` |
| RF-07 | El sistema debe permitir modificar, retrasar o cancelar vuelos, cancelando también sus reservas. | HU-07 | `PUT /api/admin/vuelos/{codigo}` · `PATCH …/estado` · `PATCH …/cancelar` |
| RF-08 | El sistema debe mostrar indicadores de operación: vuelos próximos y porcentaje de ocupación. | HU-08 | `GET /api/admin/resumen` |
| RF-09 | El sistema debe permitir buscar vuelos por origen, destino y fecha. | HU-09 | `GET /api/vuelos/buscar` |
| RF-10 | El sistema debe mostrar el detalle de un vuelo con el precio y la disponibilidad de cada clase. | HU-10 | `GET /api/vuelos/{codigo}` |
| RF-11 | El sistema debe mostrar el mapa de asientos de un vuelo indicando clase, precio y si está ocupado. | HU-11 | `GET /api/vuelos/{codigo}/asientos` |
| RF-12 | El sistema debe registrar reservas de uno o más asientos, calcular el total y generar un código único. | HU-12 | `POST /api/reservas` |
| RF-13 | El sistema debe permitir al pasajero consultar el historial de sus reservas. | HU-13 | `GET /api/reservas` |
| RF-14 | El sistema debe permitir cancelar una reserva hasta 24 horas antes de la salida y liberar sus asientos. | HU-14 | `PATCH /api/reservas/{codigo}/cancelar` |
| RF-15 | El sistema debe notificar por correo la confirmación y la cancelación de las reservas, también cuando la aerolínea cancela el vuelo. | HU-07, HU-12, HU-14, HU-24 | Sin endpoint propio: `Notificador` / `EmailNotificador` al reservar o cancelar |
| RF-16 | El sistema debe mostrar al usuario autenticado sus datos de cuenta (nombre, email y rol) sin exponer la contraseña. | HU-15 | `GET /api/auth/yo` |
| RF-17 | El sistema debe listar los aviones con su estado y su capacidad por clase. | HU-16 | `GET /api/admin/aviones` |
| RF-18 | El sistema debe permitir consultar un avión por su matrícula. | HU-17 | `GET /api/admin/aviones/{matricula}` |
| RF-19 | El sistema debe permitir actualizar el estado operativo de un vuelo (embarcando, en vuelo, aterrizado). | HU-18 | `PATCH /api/admin/vuelos/{codigo}/estado` |
| RF-20 | El sistema debe permitir filtrar los vuelos por estado y por rango de fechas. | HU-19 | `GET /api/admin/vuelos?estado=&desde=&hasta=` |
| RF-21 | El sistema debe mostrar las próximas salidas abiertas para reservar. | HU-20 | `GET /api/vuelos/proximos` |
| RF-22 | El sistema debe listar los aeropuertos donde opera la aerolínea. | HU-21 | `GET /api/aeropuertos` |
| RF-23 | El sistema debe permitir ordenar los resultados de búsqueda por precio o por hora de salida. | HU-22 | `GET /api/vuelos/buscar?…&orden=precio` |
| RF-24 | El sistema debe permitir al pasajero consultar el detalle de una de sus reservas. | HU-23 | `GET /api/reservas/{codigo}` |
