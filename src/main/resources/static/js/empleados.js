/* =========================================================
   empleados.js - listar, editar y eliminar cuentas (solo Admin)
   ========================================================= */

const usuarioSesion = requireAuth(['ADMIN']);

const tablaEmpleados = document.getElementById('tablaEmpleados');
const estadoVacioEmpleados = document.getElementById('estadoVacioEmpleados');
const modalOverlay = document.getElementById('modalOverlay');
const formEmpleado = document.getElementById('formEmpleado');

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

async function cargarEmpleados() {
  try {
    const empleados = await api.get('/empleados');
    renderTabla(empleados);
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function renderTabla(empleados) {
  tablaEmpleados.innerHTML = '';
  estadoVacioEmpleados.classList.toggle('hidden', empleados.length > 0);

  empleados.forEach((emp) => {
    const tr = document.createElement('tr');
    const esAdmin = emp.rol === 'ADMIN';
    tr.innerHTML = `
      <td>${escapeHtml(emp.nombre)}</td>
      <td class="mono">${escapeHtml(emp.username)}</td>
      <td>${escapeHtml(emp.correo)}</td>
      <td class="mono">${escapeHtml(emp.dni)}</td>
      <td class="mono">${escapeHtml(emp.celular)}</td>
      <td><span class="pill ${esAdmin ? 'pill-edicion' : 'pill-alta'}">${esAdmin ? 'Administrador' : 'Encargado'}</span></td>
      <td>
        <div class="row-actions">
          <button class="btn btn-ghost btn-icon" title="Editar" data-accion="editar" data-id="${emp.id}">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.1" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4Z"/></svg>
          </button>
          <button class="btn btn-ghost btn-icon" title="Eliminar" data-accion="eliminar" data-id="${emp.id}">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.1" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/></svg>
          </button>
        </div>
      </td>`;
    tablaEmpleados.appendChild(tr);
  });

  tablaEmpleados.querySelectorAll('[data-accion="editar"]').forEach((btn) =>
    btn.addEventListener('click', () => abrirModal(btn.dataset.id)));
  tablaEmpleados.querySelectorAll('[data-accion="eliminar"]').forEach((btn) =>
    btn.addEventListener('click', () => eliminarEmpleado(btn.dataset.id)));
}

async function abrirModal(id) {
  try {
    const empleados = await api.get('/empleados');
    const emp = empleados.find((e) => String(e.id) === String(id));
    if (!emp) return;

    document.getElementById('empleadoId').value = emp.id;
    document.getElementById('nombre').value = emp.nombre;
    document.getElementById('correo').value = emp.correo;
    document.getElementById('dni').value = emp.dni;
    document.getElementById('celular').value = emp.celular;
    document.getElementById('username').value = emp.username;
    document.getElementById('rol').value = emp.rol;
    document.getElementById('password').value = '';

    modalOverlay.classList.add('open');
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function cerrarModal() {
  modalOverlay.classList.remove('open');
}

document.getElementById('modalClose').addEventListener('click', cerrarModal);
document.getElementById('btnCancelar').addEventListener('click', cerrarModal);
modalOverlay.addEventListener('click', (e) => { if (e.target === modalOverlay) cerrarModal(); });

formEmpleado.addEventListener('submit', async (e) => {
  e.preventDefault();
  const id = document.getElementById('empleadoId').value;

  const payload = {
    nombre: document.getElementById('nombre').value.trim(),
    correo: document.getElementById('correo').value.trim(),
    dni: document.getElementById('dni').value.trim(),
    celular: document.getElementById('celular').value.trim(),
    username: document.getElementById('username').value.trim(),
    rol: document.getElementById('rol').value,
    password: document.getElementById('password').value || null,
  };

  try {
    await api.put('/empleados/' + id, payload);
    showToast('Cuenta actualizada correctamente', 'ok');
    cerrarModal();
    cargarEmpleados();
  } catch (err) {
    showToast(err.message, 'error');
  }
});

async function eliminarEmpleado(id) {
  if (!confirm('¿Eliminar esta cuenta del sistema? Esta acción no se puede deshacer.')) return;
  try {
    await api.del('/empleados/' + id);
    showToast('Cuenta eliminada', 'ok');
    cargarEmpleados();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

document.addEventListener('DOMContentLoaded', cargarEmpleados);
