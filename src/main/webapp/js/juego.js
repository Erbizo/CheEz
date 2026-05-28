const PIEZAS_INICIALES = {
    'a8': 'negras_torre',   'b8': 'negras_caballo', 'c8': 'negras_alfil',
    'd8': 'negras_dama',    'e8': 'negras_rey',     'f8': 'negras_alfil',
    'g8': 'negras_caballo', 'h8': 'negras_torre',
    'a7': 'negras_peon', 'b7': 'negras_peon', 'c7': 'negras_peon', 'd7': 'negras_peon',
    'e7': 'negras_peon', 'f7': 'negras_peon', 'g7': 'negras_peon', 'h7': 'negras_peon',
    'a2': 'blancas_peon', 'b2': 'blancas_peon', 'c2': 'blancas_peon', 'd2': 'blancas_peon',
    'e2': 'blancas_peon', 'f2': 'blancas_peon', 'g2': 'blancas_peon', 'h2': 'blancas_peon',
    'a1': 'blancas_torre',   'b1': 'blancas_caballo', 'c1': 'blancas_alfil',
    'd1': 'blancas_dama',    'e1': 'blancas_rey',     'f1': 'blancas_alfil',
    'g1': 'blancas_caballo', 'h1': 'blancas_torre'
};

const COLUMNAS = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
const ID_PARTIDA = parseInt(new URLSearchParams(window.location.search).get('id'), 10);
const MI_ALIAS = sessionStorage.getItem('alias');

var aliasBlancas = '';
var aliasNegras  = '';
var esMiTurno    = false;
var casillaSeleccionada = null;
var totalMovimientosConocidos = -1;
var partidaTerminada = false;
var enJaque       = false;
var posReyEnJaque = null;
var pollingId     = null;
var _avisoTimer   = null;
var resultadoMostrado = null;
var _navegandoDentroDelApp = false;

window.addEventListener('beforeunload', function() {
    if (!_navegandoDentroDelApp) {
        navigator.sendBeacon('/logout');
    }
});

function volverAlLobby() {
    _navegandoDentroDelApp = true;
    location.href = 'lobby.html';
}

function mostrarAvisoInvalido() {
    var aviso = document.getElementById('aviso-invalido');
    if (!aviso) return;
    aviso.classList.remove('visible');
    void aviso.offsetWidth;
    aviso.classList.add('visible');
    if (_avisoTimer !== null) clearTimeout(_avisoTimer);
    _avisoTimer = setTimeout(function() {
        aviso.classList.remove('visible');
        _avisoTimer = null;
    }, 2000);
}

function colIdx(pos) { return pos.charCodeAt(0) - 97; }
function filIdx(pos) { return pos.charCodeAt(1) - 49; }
function toPos(c, f) { return String.fromCharCode(97 + c) + String.fromCharCode(49 + f); }

function hayObstaculosTorre(tablero, o, d) {
    var dc = colIdx(d) - colIdx(o), df = filIdx(d) - filIdx(o);
    var sc = Math.sign(dc), sf = Math.sign(df);
    var c = colIdx(o) + sc, f = filIdx(o) + sf;
    while (c !== colIdx(d) || f !== filIdx(d)) {
        if (tablero[toPos(c, f)]) return true;
        c += sc; f += sf;
    }
    return false;
}

function hayObstaculosAlfil(tablero, o, d) {
    var dc = colIdx(d) - colIdx(o), df = filIdx(d) - filIdx(o);
    if (Math.abs(dc) !== Math.abs(df)) return false;
    var sc = Math.sign(dc), sf = Math.sign(df);
    var c = colIdx(o) + sc, f = filIdx(o) + sf;
    while (c !== colIdx(d) || f !== filIdx(d)) {
        if (tablero[toPos(c, f)]) return true;
        c += sc; f += sf;
    }
    return false;
}

function puedeAtacar(tablero, origen, destino) {
    var pieza = tablero[origen];
    if (!pieza || origen === destino) return false;
    var tipo   = pieza.split('_')[1];
    var esBlanca = pieza.startsWith('blancas');
    var dc = colIdx(destino) - colIdx(origen);
    var df = filIdx(destino) - filIdx(origen);

    switch (tipo) {
        case 'peon': {
            var dir = esBlanca ? 1 : -1;
            return Math.abs(dc) === 1 && df === dir;
        }
        case 'torre':
            if (dc !== 0 && df !== 0) return false;
            return !hayObstaculosTorre(tablero, origen, destino);
        case 'alfil':
            if (Math.abs(dc) !== Math.abs(df)) return false;
            return !hayObstaculosAlfil(tablero, origen, destino);
        case 'dama':
            if (dc !== 0 && df !== 0 && Math.abs(dc) !== Math.abs(df)) return false;
            if (dc === 0 || df === 0) return !hayObstaculosTorre(tablero, origen, destino);
            return !hayObstaculosAlfil(tablero, origen, destino);
        case 'caballo': {
            var adc = Math.abs(dc), adf = Math.abs(df);
            return (adc === 2 && adf === 1) || (adc === 1 && adf === 2);
        }
        case 'rey':
            return Math.abs(dc) <= 1 && Math.abs(df) <= 1;
        default: return false;
    }
}

function encontrarRey(tablero, esBlancas) {
    var buscado = esBlancas ? 'blancas_rey' : 'negras_rey';
    return Object.keys(tablero).find(function(pos) { return tablero[pos] === buscado; }) || null;
}

function reyEnJaque(tablero, esBlancas) {
    var posRey = encontrarRey(tablero, esBlancas);
    if (!posRey) return false;
    var prefEnemigo = esBlancas ? 'negras' : 'blancas';
    return Object.keys(tablero).some(function(pos) {
        return tablero[pos].startsWith(prefEnemigo) && puedeAtacar(tablero, pos, posRey);
    });
}

function aplicarLayoutMovil() {
    if (!aliasBlancas || !aliasNegras) return;
    var accionesEl = document.getElementById('acciones-local');
    var panelRival = document.getElementById('panel-rival');
    var panelLocal = document.getElementById('panel-local');
    if (!accionesEl || !panelRival || !panelLocal) return;

    var esMobile = window.matchMedia('(max-width: 1100px)').matches;
    var soyNegras = (MI_ALIAS === aliasNegras);

    if (esMobile && soyNegras) {
        if (accionesEl.parentElement !== panelRival) {
            panelRival.appendChild(accionesEl);
        }
    } else {
        if (accionesEl.parentElement !== panelLocal) {
            panelLocal.appendChild(accionesEl);
        }
    }
}

function generarTablero() {
    var tablero = document.getElementById('tablero');
    for (var fila = 8; fila >= 1; fila--) {
        for (var col = 0; col < 8; col++) {
            var pos = COLUMNAS[col] + fila;
            var casilla = document.createElement('div');
            casilla.classList.add('casilla');
            casilla.classList.add((col + fila) % 2 === 0 ? 'clara' : 'oscura');
            casilla.dataset.pos = pos;
            casilla.id = 'casilla-' + pos;
            casilla.addEventListener('click', manejarClic);
            tablero.appendChild(casilla);
        }
    }
}

function reconstruirTablero(movimientos) {
    var estado = Object.assign({}, PIEZAS_INICIALES);
    movimientos.forEach(function(mov) {
        var p = mov.movimiento.split('-');
        estado[p[1]] = estado[p[0]];
        delete estado[p[0]];
    });

    document.querySelectorAll('.casilla.en-jaque').forEach(function(c) {
        c.classList.remove('en-jaque');
    });

    COLUMNAS.forEach(function(c) {
        for (var f = 1; f <= 8; f++) {
            var pos = c + f;
            var cas = document.getElementById('casilla-' + pos);
            var img = cas.querySelector('.pieza');
            if (img) cas.removeChild(img);
            if (estado[pos]) {
                var nueva = document.createElement('img');
                nueva.src = 'img/piezas/' + estado[pos] + '.png';
                nueva.alt = estado[pos];
                nueva.classList.add('pieza');
                cas.appendChild(nueva);
            }
        }
    });

    var turnoBlancas = movimientos.length % 2 === 0;
    esMiTurno = turnoBlancas ? (MI_ALIAS === aliasBlancas) : (MI_ALIAS === aliasNegras);

    document.getElementById('nombre-blancas').classList.toggle('turno-activo', turnoBlancas);
    document.getElementById('nombre-negras').classList.toggle('turno-activo', !turnoBlancas);

    var colorEnTurno = turnoBlancas;
    enJaque = reyEnJaque(estado, colorEnTurno);
    posReyEnJaque = enJaque ? encontrarRey(estado, colorEnTurno) : null;

    if (enJaque && posReyEnJaque) {
        var casRey = document.getElementById('casilla-' + posReyEnJaque);
        casRey.classList.remove('en-jaque');
        void casRey.offsetWidth;
        casRey.classList.add('en-jaque');
    }

    totalMovimientosConocidos = movimientos.length;
}

function sincronizarPartida() {
    if (!ID_PARTIDA || partidaTerminada) return;
    fetch('/partida?id=' + ID_PARTIDA)
    .then(function(r) { return r.json(); })
    .then(function(datos) {
        var movimientos = datos.movimientos || [];
        if (movimientos.length !== totalMovimientosConocidos) {
            reconstruirTablero(movimientos);
        }
        var res = datos.partida && datos.partida.resultado;
        if (!res || res === 'en_curso') {
            fetch('/tablas?id=' + ID_PARTIDA)
            .then(function(r2) { return r2.json(); })
            .then(function(t) {
                if (t.ofertante && t.ofertante !== MI_ALIAS) {
                    mostrarOfertaTablas(t.ofertante);
                } else if (!t.ofertante && resultadoMostrado && resultadoMostrado.startsWith('oferta_')) {
                    _ocultarModal();
                    resultadoMostrado = null;
                }
            })
            .catch(function(err) { console.error('Error al consultar tablas:', err); });
            return;
        }
        mostrarFinPartida(res);
    })
    .catch(function(err) { console.error('Error al sincronizar partida:', err); });
}

function manejarClic(e) {
    if (!esMiTurno || partidaTerminada) return;

    var casilla = e.currentTarget;
    var pieza   = casilla.querySelector('.pieza');

    if (casillaSeleccionada === null) {
        if (!pieza) return;
        casillaSeleccionada = casilla;
        casilla.classList.add('seleccionada');
        return;
    }

    if (casilla === casillaSeleccionada) {
        casilla.classList.remove('seleccionada');
        casillaSeleccionada = null;
        return;
    }

    var origen  = casillaSeleccionada.dataset.pos;
    var destino = casilla.dataset.pos;
    casillaSeleccionada.classList.remove('seleccionada');
    casillaSeleccionada = null;

    esMiTurno = false;

    fetch('/juego', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ idPartida: ID_PARTIDA, origen: origen, destino: destino })
    })
    .then(function(r) { return r.json(); })
    .then(function(datos) {
        if (datos.resultado === 'valido') {
            sincronizarPartida();
        } else {
            esMiTurno = true;
            mostrarAvisoInvalido();
        }
    })
    .catch(function(err) {
        esMiTurno = true;
        console.error('Error al enviar movimiento:', err);
    });
}

function _mostrarModal(titulo, borroRojo, botonesVisibles) {
    document.getElementById('modal-titulo').textContent = titulo;
    var caja = document.getElementById('modal-caja');
    caja.classList.toggle('borde-rojo', borroRojo);

    document.getElementById('btn-volver-lobby').style.display   = botonesVisibles.lobby   ? '' : 'none';
    document.getElementById('btn-aceptar-tablas').style.display = botonesVisibles.aceptar ? '' : 'none';
    document.getElementById('btn-rechazar-tablas').style.display= botonesVisibles.rechazar? '' : 'none';

    document.getElementById('btn-volver-lobby').textContent = 'Volver';

    caja.style.animation = 'none';
    void caja.offsetWidth;
    caja.style.animation = '';

    document.getElementById('modal-fin').classList.remove('oculto');
}

function _ocultarModal() {
    document.getElementById('modal-fin').classList.add('oculto');
}

function mostrarFinPartida(resultado) {
    if (resultadoMostrado === resultado) return;
    resultadoMostrado = resultado;

    partidaTerminada = true;
    esMiTurno = false;
    enJaque = false;
    if (pollingId !== null) { clearInterval(pollingId); pollingId = null; }

    var titulo, esDerrota;
    if (resultado === 'tablas') {
        titulo = 'La partida ha acabado en empate';
        esDerrota = false;
    } else {
        var ganador = resultado === 'blancas' ? aliasBlancas : aliasNegras;
        titulo = 'Ha ganado "' + ganador + '"';
        esDerrota = (ganador !== MI_ALIAS);
    }
    _mostrarModal(titulo, esDerrota, { lobby: true, aceptar: false, rechazar: false });
}

function mostrarOfertaTablas(ofertante) {
    if (resultadoMostrado === 'oferta_tablas_' + ofertante) return;
    resultadoMostrado = 'oferta_tablas_' + ofertante;
    _mostrarModal(
        '"' + ofertante + '" ha ofrecido tablas',
        false,
        { lobby: false, aceptar: true, rechazar: true }
    );
}

function responderTablas(aceptar) {
    _ocultarModal();
    resultadoMostrado = null;
    if (aceptar) {
        fetch('/tablas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idPartida: ID_PARTIDA, accion: 'retirar' })
        })
        .catch(function(err) { console.error('Error al retirar oferta de tablas:', err); });
        fetch('/partida', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idPartida: ID_PARTIDA, resultado: 'tablas' })
        })
        .then(function() { sincronizarPartida(); })
        .catch(function(err) { console.error('Error al guardar resultado de tablas:', err); });
    } else {
        fetch('/tablas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idPartida: ID_PARTIDA, accion: 'retirar' })
        })
        .catch(function(err) { console.error('Error al rechazar tablas:', err); });
    }
}

function cargarPartida() {
    if (!ID_PARTIDA) return;
    fetch('/partida?id=' + ID_PARTIDA)
    .then(function(r) { return r.json(); })
    .then(function(datos) {
        if (datos.partida) {
            aliasBlancas = datos.partida.aliasBlancas;
            aliasNegras  = datos.partida.aliasNegras;
            document.getElementById('nombre-blancas').textContent = aliasBlancas;
            document.getElementById('nombre-negras').textContent  = aliasNegras;
            aplicarLayoutMovil();
        }
        reconstruirTablero(datos.movimientos || []);
        var res = datos.partida && datos.partida.resultado;
        if (res && res !== 'en_curso') {
            sincronizarPartida();
        }
    })
    .catch(function(err) { console.error('Error al cargar partida:', err); });
}

document.addEventListener('DOMContentLoaded', function() {
    if (MI_ALIAS) document.getElementById('alias-actual').textContent = MI_ALIAS;
    generarTablero();
    cargarPartida();
    pollingId = setInterval(sincronizarPartida, 2000);
    window.addEventListener('resize', aplicarLayoutMovil);

    document.getElementById('btn-tablas').addEventListener('click', function() {
        if (partidaTerminada) return;
        fetch('/tablas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idPartida: ID_PARTIDA, accion: 'ofrecer' })
        })
        .then(function(r) { return r.json(); })
        .then(function(d) {
            if (d.ok) {
                document.getElementById('btn-tablas').disabled = true;
                document.getElementById('btn-tablas').textContent = 'Tablas ofrecidas...';
            }
        })
        .catch(function(err) { console.error('Error al ofrecer tablas:', err); });
    });

    document.getElementById('btn-rendirse').addEventListener('click', function() {
        if (partidaTerminada) return;
        var ganadorResultado = MI_ALIAS === aliasBlancas ? 'negras' : 'blancas';
        fetch('/partida', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idPartida: ID_PARTIDA, resultado: ganadorResultado })
        })
        .then(function() { sincronizarPartida(); })
        .catch(function(err) { console.error('Error al rendirse:', err); });
    });
});
