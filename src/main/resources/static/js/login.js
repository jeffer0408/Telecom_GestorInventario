/* =========================================================
   login.js
   ========================================================= */

// Si ya hay sesion activa, no tiene sentido ver el login de nuevo
if (getUsuarioActual()) {
  window.location.href = 'index.html';
}

document.getElementById('formLogin').addEventListener('submit', async (e) => {
  e.preventDefault();

  const payload = {
    username: document.getElementById('username').value.trim(),
    password: document.getElementById('password').value,
  };

  try {
    const usuario = await api.post('/auth/login', payload);
    guardarSesion(usuario);
    window.location.href = 'index.html';
  } catch (err) {
    showToast(err.message, 'error');
  }
});
