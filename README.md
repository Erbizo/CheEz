# CheEz - Ajedrez multijugador en tiempo real

Plataforma web de ajedrez multijugador desarrollada con Jakarta EE, siguiendo el patron de diseno MVC, con peticiones asincronas mediante promesas (fetch/JSON) y base de datos MySQL. Desplegada mediante Docker Compose.

## Tecnologias

- **Servidor**: Tomcat 10 (Jakarta Servlet 6.0)
- **Backend**: Java + Servlets (patron MVC)
- **Frontend**: HTML, CSS y JavaScript
- **Base de datos**: MySQL 8
- **Gestor visual**: phpMyAdmin
- **Despliegue**: Docker + Docker Compose

## Estructura del proyecto

```
jakartaee-docker-compose-project/
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── informacion.md
├── README.md
├── mysql/
│   └── init/
│       └── 01-bd1.sql
└── src/
    └── main/
        ├── java/
        │   └── com/ejemplo/
        │       ├── controller/
        │       │   ├── LoginServlet.java
        │       │   ├── LogoutServlet.java
        │       │   ├── LobbyServlet.java
        │       │   ├── SolicitudServlet.java
        │       │   ├── PartidaServlet.java
        │       │   ├── JuegoServlet.java
        │       │   ├── TablasServlet.java
        │       │   └── SessionListener.java
        │       └── model/
        │           ├── ConexionBD.java
        │           ├── Jugador.java
        │           ├── JugadorDAO.java
        │           ├── Partida.java
        │           ├── PartidaDAO.java
        │           ├── Movimiento.java
        │           ├── MovimientoDAO.java
        │           ├── Solicitud.java
        │           ├── SolicitudDAO.java
        │           └── ValidadorMovimiento.java
        └── webapp/
            ├── css/
            │   ├── general.css
            │   ├── login.css
            │   ├── lobby.css
            │   └── juego.css
            ├── js/
            │   ├── login.js
            │   ├── lobby.js
            │   └── juego.js
            ├── img/
            │   └── piezas/
            ├── WEB-INF/
            │   └── web.xml
            ├── index.html
            ├── lobby.html
            └── juego.html
```

## Puesta en marcha

Desde la carpeta del proyecto:

```bash
docker compose up --build
```

## URLs

- Aplicacion web: `http://localhost:8080`
- phpMyAdmin: `http://localhost:8081`
  - Usuario: `root`
  - Contrasena: `root`
- MySQL desde el host: `localhost:3307`
  - Base de datos: `cheezdb`
  - Usuario: `root`
  - Contrasena: `root`

## Flujo de la aplicacion

1. El jugador accede a `index.html` e introduce su alias.
2. El frontend envia un `POST /login` con JSON al servlet.
3. El servidor registra al jugador en la BD y crea una sesion HTTP.
4. Se redirige al lobby (`lobby.html`) donde se listan los jugadores activos.
5. Un jugador puede invitar a otro; la invitacion se gestiona via `POST /solicitud`.
6. Al aceptar una invitacion se crea una partida y ambos jugadores van a `juego.html`.
7. Los movimientos se envian via `POST /juego` y el servidor los valida con `ValidadorMovimiento`.
8. El estado de la partida se sincroniza mediante polling (`GET /partida`) cada 2 segundos.
9. Al cerrar el navegador, `navigator.sendBeacon('/logout')` invalida la sesion y elimina al jugador de la BD.

## Ejemplo de comunicacion JSON

### Login (POST /login)
```json
{ "alias": "Jugador1" }
```

### Movimiento (POST /juego)
```json
{ "idPartida": 1, "origen": "e2", "destino": "e4" }
```

### Respuesta del servidor
```json
{ "resultado": "valido", "jaque": false, "jaqueMate": false, "ahogado": false, "reyCapturado": false }
```
