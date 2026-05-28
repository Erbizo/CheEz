var INTERVALO_POLLING = 2000;
var _navegandoDentroDelApp = false;

window.addEventListener('beforeunload', function() {
    if (!_navegandoDentroDelApp) {
        navigator.sendBeacon('/logout');
    }
});

function cargarJugadores() {
    fetch('/lobby')
    .then(function(r) {
        if (r.status === 401) { window.location.href = 'index.html'; return null; }
        return r.json();
    })
    .then(function(jugadores) {
        if (!jugadores) return;
        var lista = document.getElementById('lista-jugadores');
        lista.innerHTML = '';
        jugadores.forEach(function(j) {
            var item = document.createElement('div');
            item.classList.add('item-lista');
            var nombre = document.createElement('span');
            nombre.textContent = j.alias;
            var btn = document.createElement('button');
            btn.classList.add('boton-verde');
            btn.textContent = 'Invitar';
            btn.addEventListener('click', function() { enviarSolicitud(j.alias); });
            item.appendChild(nombre);
            item.appendChild(btn);
            lista.appendChild(item);
        });
    })
    .catch(function(err) { console.error('Error al cargar jugadores:', err); });
}

function cargarInvitaciones() {
    fetch('/solicitud')
    .then(function(r) { return r.json(); })
    .then(function(solicitudes) {
        var lista = document.getElementById('lista-invitaciones');
        lista.innerHTML = '';
        solicitudes.forEach(function(s) {
            var item = document.createElement('div');
            item.classList.add('item-lista');
            var nombre = document.createElement('span');
            nombre.textContent = s.aliasEmisor;
            var acciones = document.createElement('div');
            acciones.classList.add('acciones-item');
            var btnAceptar = document.createElement('button');
            btnAceptar.classList.add('boton-icono', 'verde');
            btnAceptar.textContent = '✓';
            btnAceptar.addEventListener('click', function() { responderSolicitud(s.id, 'aceptada'); });
            var btnRechazar = document.createElement('button');
            btnRechazar.classList.add('boton-icono', 'rojo');
            btnRechazar.textContent = '✕';
            btnRechazar.addEventListener('click', function() { responderSolicitud(s.id, 'rechazada'); });
            acciones.appendChild(btnAceptar);
            acciones.appendChild(btnRechazar);
            item.appendChild(nombre);
            item.appendChild(acciones);
            lista.appendChild(item);
        });
    })
    .catch(function(err) { console.error('Error al cargar invitaciones:', err); });
}

function enviarSolicitud(aliasReceptor) {
    fetch('/solicitud', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ accion: 'enviar', aliasReceptor: aliasReceptor })
    })
    .then(function(r) { return r.json(); })
    .then(function(datos) {
        if (!datos.ok) console.error('No se pudo enviar la solicitud');
    })
    .catch(function(err) { console.error('Error al enviar solicitud:', err); });
}

function responderSolicitud(idSolicitud, respuesta) {
    fetch('/solicitud', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ accion: 'responder', idSolicitud: idSolicitud, respuesta: respuesta })
    })
    .then(function(r) { return r.json(); })
    .then(function(datos) {
        if (datos.ok && datos.idPartida) {
            _navegandoDentroDelApp = true;
            window.location.href = 'juego.html?id=' + datos.idPartida;
        } else {
            cargarInvitaciones();
        }
    })
    .catch(function(err) { console.error('Error al responder solicitud:', err); });
}

function cargarPartidaActiva() {
    fetch('/partida?activa=true')
    .then(function(r) { return r.json(); })
    .then(function(datos) {
        if (datos && datos.idPartida) {
            _navegandoDentroDelApp = true;
            window.location.href = 'juego.html?id=' + datos.idPartida;
        }
    })
    .catch(function(err) { console.error('Error al comprobar partida activa:', err); });
}

function iniciarPolling() {
    cargarJugadores();
    cargarInvitaciones();
    cargarPartidaActiva();
    setInterval(function() {
        cargarJugadores();
        cargarInvitaciones();
        cargarPartidaActiva();
    }, INTERVALO_POLLING);
}

document.addEventListener('DOMContentLoaded', function() {
    var aliasActual = sessionStorage.getItem('alias');
    if (aliasActual) document.getElementById('alias-actual').textContent = aliasActual;
    iniciarPolling();
});
