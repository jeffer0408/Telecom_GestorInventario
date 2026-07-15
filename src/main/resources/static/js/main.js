/* =========================================================
   main.js - logica compartida entre todas las paginas
   ========================================================= */

const API_BASE = '/api';
const CLAVE_SESION = 'usuarioActual';

/* ---------- Sesion / usuario conectado ---------- */
function getUsuarioActual() {
  try {
    const datos = sessionStorage.getItem(CLAVE_SESION);
    return datos ? JSON.parse(datos) : null;
  } catch (_) {
    return null;
  }
}

function guardarSesion(usuario) {
  sessionStorage.setItem(CLAVE_SESION, JSON.stringify(usuario));
}

function cerrarSesion() {
  sessionStorage.removeItem(CLAVE_SESION);
  window.location.href = 'login.html';
}

/**
 * Protege una pagina: si no hay sesion, redirige al login.
 * Si se pasan roles permitidos y el usuario no cumple, lo manda al inventario.
 * Tambien pinta el nombre/rol en la barra superior y el enlace de Admin en el menu.
 */
function requireAuth(rolesPermitidos = null) {
  const usuario = getUsuarioActual();
  if (!usuario) {
    window.location.href = 'login.html';
    return null;
  }
  if (rolesPermitidos && !rolesPermitidos.includes(usuario.rol)) {
    window.location.href = 'index.html';
    return null;
  }
  pintarIdentidad(usuario);
  return usuario;
}

function pintarIdentidad(usuario) {
  const nombreEl = document.getElementById('usuarioNombre');
  const rolEl = document.getElementById('usuarioRol');
  if (nombreEl) nombreEl.textContent = usuario.nombre;
  if (rolEl) rolEl.textContent = usuario.rol === 'ADMIN' ? 'Administrador' : 'Encargado de almacen';

  const linkAdmin = document.getElementById('linkAdminEmpleados');
  if (linkAdmin) linkAdmin.classList.toggle('hidden', usuario.rol !== 'ADMIN');

  document.getElementById('btnCerrarSesion')?.addEventListener('click', cerrarSesion);
}

/* ---------- Menu hamburguesa (drawer) ---------- */
function initDrawer() {
  const drawer = document.getElementById('drawer');
  const overlay = document.getElementById('drawerOverlay');
  const openBtn = document.getElementById('btnHamburger');
  const closeBtn = document.getElementById('drawerClose');

  const open = () => { drawer.classList.add('open'); overlay.classList.add('open'); };
  const close = () => { drawer.classList.remove('open'); overlay.classList.remove('open'); };

  openBtn?.addEventListener('click', open);
  closeBtn?.addEventListener('click', close);
  overlay?.addEventListener('click', close);
  document.addEventListener('keydown', (e) => { if (e.key === 'Escape') close(); });
}

/* ---------- Toast de notificaciones ---------- */
let toastTimer = null;
function showToast(mensaje, tipo = 'default') {
  const toast = document.getElementById('toast');
  if (!toast) return;
  toast.textContent = mensaje;
  toast.className = 'toast show' + (tipo === 'error' ? ' err' : tipo === 'ok' ? ' ok' : '');
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.remove('show'), 3200);
}

/* ---------- Helper fetch con manejo de errores ---------- */
async function apiRequest(path, options = {}) {
  const usuario = getUsuarioActual();
  const headers = { 'Content-Type': 'application/json' };
  if (usuario) {
    headers['X-Rol'] = usuario.rol;
    headers['X-Usuario-Id'] = usuario.id;
  }

  const res = await fetch(API_BASE + path, {
    headers,
    ...options,
  });

  if (!res.ok) {
    let mensaje = 'Ocurrio un error al procesar la solicitud';
    try {
      const data = await res.json();
      mensaje = data.mensaje || Object.values(data)[0] || mensaje;
    } catch (_) { /* respuesta sin cuerpo JSON */ }
    throw new Error(mensaje);
  }

  if (res.status === 204) return null;
  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

const api = {
  get: (path) => apiRequest(path),
  post: (path, body) => apiRequest(path, { method: 'POST', body: JSON.stringify(body) }),
  put: (path, body) => apiRequest(path, { method: 'PUT', body: JSON.stringify(body) }),
  del: (path) => apiRequest(path, { method: 'DELETE' }),
};

/* ---------- Formato de numeros / moneda ---------- */
function formatoMoneda(valor) {
  return 'S/ ' + Number(valor ?? 0).toLocaleString('es-PE', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}
function formatoNumero(valor) {
  return Number(valor ?? 0).toLocaleString('es-PE', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
}
function formatoFecha(iso) {
  if (!iso) return '-';
  const d = new Date(iso);
  return d.toLocaleString('es-PE', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

document.addEventListener('DOMContentLoaded', initDrawer);
