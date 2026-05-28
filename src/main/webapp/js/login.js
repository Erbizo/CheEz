document.addEventListener('DOMContentLoaded', function() {
    var btn = document.getElementById('btn-aceptar');
    var input = document.getElementById('input-alias');
    var error = document.getElementById('mensaje-error');

    btn.addEventListener('click', function() {
        var alias = input.value.trim();
        if (alias.length === 0 || alias.length > 8) {
            error.textContent = 'El alias debe tener entre 1 y 8 caracteres.';
            error.style.display = 'block';
            return;
        }
        fetch('/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ alias: alias })
        })
        .then(function(r) { return r.json(); })
        .then(function(datos) {
            if (datos.ok) {
                sessionStorage.setItem('alias', alias);
                window.location.href = 'lobby.html';
            } else {
                error.textContent = datos.mensaje || 'Error al iniciar sesion.';
                error.style.display = 'block';
            }
        })
        .catch(function(err) {
            error.textContent = 'Error de conexion con el servidor.';
            error.style.display = 'block';
        });
    });
});
