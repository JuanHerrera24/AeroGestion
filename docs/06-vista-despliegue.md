# Vista 5: Despliegue

**Objetivo:** mostrar en qué nodos físicos se ejecuta cada parte del sistema y cómo se comunican.

![VistaDespliegue](../diagramas/Vista%20de%20Despliegue.png)

## Nodos

| Nodo `<<device>>` | Entorno `<<executionEnvironment>>` | Artefactos `<<artifact>>` |
|---|---|---|
| PC del usuario (Pasajero / Visitante) | Postman (cliente REST) | `AeroGestion.postman_collection.json` |
| PC del Administrador | Postman (cliente REST) | `AeroGestion.postman_collection.json` |
| Servidor de Aplicaciones | JVM Java 21 + Tomcat embebido (puerto 8080) | `aerogestion-0.1.0.jar` (Spring Boot 3), `application.properties` |
| Servidor de Base de Datos | MongoDB 8.0 | Base de datos `aerogestion` (colecciones `usuarios`, `aviones`, `aeropuertos`, `vuelos`, `reservas`) |
| Servidor de correo `<<external>>` | Servicio SMTP (Gmail) | — |

## Conexiones

| Desde → Hacia | Protocolo | Puerto |
|---|---|---|
| PC del usuario → Servidor de Aplicaciones | HTTP / REST JSON (HTTPS en producción) | 8080 (443) |
| PC del Administrador → Servidor de Aplicaciones | HTTP / REST JSON (HTTPS en producción) | 8080 (443) |
| Servidor de Aplicaciones → Servidor de Base de Datos | TCP / MongoDB Wire Protocol | 27017 |
| Servidor de Aplicaciones → Servidor de correo | SMTP + TLS | 587 |

## Decisiones de despliegue

- **Los clientes nunca se conectan a MongoDB.** Toda petición entra por la API, que
  autentica, valida las reglas de negocio y decide qué guardar. Si Postman o un navegador
  hablaran directo con la base de datos, cualquiera podría saltarse las reglas
  (por ejemplo, ocupar un asiento ya reservado).
- **Un solo artefacto ejecutable.** `aerogestion-0.1.0.jar` incluye Tomcat embebido, así que
  se despliega con `java -jar` sin instalar un servidor web aparte.
- **Configuración fuera del código.** La URI de MongoDB y la cuenta SMTP están en
  `application.properties` y se pueden cambiar por variables de entorno
  (`SPRING_DATA_MONGODB_URI`, `SPRING_MAIL_USERNAME`…) sin recompilar.
- **El correo es externo y no bloquea.** Si no hay cuenta SMTP configurada, los correos se
  simulan en la consola y la reserva igual se confirma.
