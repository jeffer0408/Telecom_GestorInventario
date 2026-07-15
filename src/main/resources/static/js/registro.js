/* =========================================================
   registro.js
   ========================================================= */

if (getUsuarioActual()) {
  window.location.href = 'index.html';
}

const btnEnviarCodigo = document.getElementById('btnEnviarCodigo');

btnEnviarCodigo.addEventListener('click', async () => {
  const correo = document.getElementById('correo').value.trim();
  if (!correo) {
    showToast('Escribe tu correo antes de solicitar el código', 'error');
    return;
  }

  btnEnviarCodigo.disabled = true;
  btnEnviarCodigo.textContent = 'Enviando...';

  try {
    await api.post('/auth/codigo', { correo });
    showToast('Código enviado al correo del Admin. Pídeselo para continuar.', 'ok');
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btnEnviarCodigo.disabled = false;
    btnEnviarCodigo.textContent = 'Enviar código al Admin';
  }
});

document.getElementById('formRegistro').addEventListener('submit', async (e) => {
  e.preventDefault();

  const password = document.getElementById('password').value;
  const password2 = document.getElementById('password2').value;
  if (password !== password2) {
    showToast('Las contraseñas no coinciden', 'error');
    return;
  }

  const payload = {
    nombre: document.getElementById('nombre').value.trim(),
    correo: document.getElementById('correo').value.trim(),
    dni: document.getElementById('dni').value.trim(),
    celular: document.getElementById('celular').value.trim(),
    username: document.getElementById('username').value.trim(),
    password,
    codigo: document.getElementById('codigo').value.trim(),
  };

  try {
    await api.post('/auth/registro', payload);
    showToast('Cuenta creada correctamente. Ya puedes iniciar sesión.', 'ok');
    setTimeout(() => { window.location.href = 'login.html'; }, 1200);
  } catch (err) {
    showToast(err.message, 'error');
  }
});
