/* =========================================================
   ajustes.js - CRUD simple de proveedores y tipos de almacen
   ========================================================= */

requireAuth();

const listaProveedores = document.getElementById('listaProveedores');
const listaTipos = document.getElementById('listaTipos');

function iconoBasura() {
  return `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.1" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/></svg>`;
}

async function cargarProveedores() {
  try {
    const items = await api.get('/proveedores');
    renderLista(listaProveedores, items, 'proveedores');
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function cargarTipos() {
  try {
    const items = await api.get('/tipos-almacen');
    renderLista(listaTipos, items, 'tipos-almacen');
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function renderLista(ul, items, recurso) {
  ul.innerHTML = '';
  if (items.length === 0) {
    ul.innerHTML = '<li class="text-muted">Aun no hay registros.</li>';
    return;
  }
  items.forEach((item) => {
    const li = document.createElement('li');
    const nombre = document.createElement('span');
    nombre.textContent = item.nombre;

    const btn = document.createElement('button');
    btn.className = 'btn btn-ghost btn-icon';
    btn.title = 'Eliminar';
    btn.innerHTML = iconoBasura();
    btn.addEventListener('click', () => eliminarItem(recurso, item.id));

    li.appendChild(nombre);
    li.appendChild(btn);
    ul.appendChild(li);
  });
}

async function eliminarItem(recurso, id) {
  if (!confirm('¿Eliminar este registro? No podra usarse en nuevos productos.')) return;
  try {
    await api.del('/' + recurso + '/' + id);
    showToast('Registro eliminado', 'ok');
    recurso === 'proveedores' ? cargarProveedores() : cargarTipos();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

document.getElementById('formProveedor').addEventListener('submit', async (e) => {
  e.preventDefault();
  const input = document.getElementById('nuevoProveedor');
  const nombre = input.value.trim();
  if (!nombre) return;
  try {
    await api.post('/proveedores', { nombre });
    input.value = '';
    showToast('Proveedor agregado', 'ok');
    cargarProveedores();
  } catch (err) {
    showToast(err.message, 'error');
  }
});

document.getElementById('formTipo').addEventListener('submit', async (e) => {
  e.preventDefault();
  const input = document.getElementById('nuevoTipo');
  const nombre = input.value.trim();
  if (!nombre) return;
  try {
    await api.post('/tipos-almacen', { nombre });
    input.value = '';
    showToast('Tipo de almacen agregado', 'ok');
    cargarTipos();
  } catch (err) {
    showToast(err.message, 'error');
  }
});

document.addEventListener('DOMContentLoaded', () => {
  cargarProveedores();
  cargarTipos();
});
