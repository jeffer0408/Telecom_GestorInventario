/* =========================================================
   inventario.js - tabla principal, busqueda y CRUD de productos
   ========================================================= */

requireAuth();

let proveedores = [];
let tiposAlmacen = [];

const tablaBody = document.getElementById('tablaBody');
const estadoVacio = document.getElementById('estadoVacio');

const modalOverlay = document.getElementById('modalOverlay');
const modalTitulo = document.getElementById('modalTitulo');
const formProducto = document.getElementById('formProducto');

const inputBuscar = document.getElementById('inputBuscar');
const filtroProveedor = document.getElementById('filtroProveedor');
const filtroTipo = document.getElementById('filtroTipo');

let debounceTimer = null;

/* ---------- Carga inicial ---------- */
async function init() {
  await cargarCatalogos();
  await cargarProductos();

  document.getElementById('btnNuevo').addEventListener('click', () => abrirModal());
  document.getElementById('modalClose').addEventListener('click', cerrarModal);
  document.getElementById('btnCancelar').addEventListener('click', cerrarModal);
  modalOverlay.addEventListener('click', (e) => { if (e.target === modalOverlay) cerrarModal(); });

  formProducto.addEventListener('submit', guardarProducto);

  inputBuscar.addEventListener('input', () => {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(cargarProductos, 280);
  });
  filtroProveedor.addEventListener('change', cargarProductos);
  filtroTipo.addEventListener('change', cargarProductos);

  document.getElementById('btnLimpiarFiltros').addEventListener('click', () => {
    inputBuscar.value = '';
    filtroProveedor.value = '';
    filtroTipo.value = '';
    cargarProductos();
  });
}

async function cargarCatalogos() {
  try {
    [proveedores, tiposAlmacen] = await Promise.all([
      api.get('/proveedores'),
      api.get('/tipos-almacen'),
    ]);

    llenarSelect(filtroProveedor, proveedores, 'Todos los proveedores');
    llenarSelect(filtroTipo, tiposAlmacen, 'Todos los tipos de almacen');

    llenarSelect(document.getElementById('proveedor'), proveedores, 'Selecciona un proveedor', true);
    llenarSelect(document.getElementById('tipoAlmacen'), tiposAlmacen, 'Selecciona un tipo', true);
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function llenarSelect(select, items, placeholder, requerido = false) {
  const valorActual = select.value;
  select.innerHTML = '';
  const optPlaceholder = document.createElement('option');
  optPlaceholder.value = '';
  optPlaceholder.textContent = placeholder;
  if (requerido) optPlaceholder.disabled = true;
  select.appendChild(optPlaceholder);

  items.forEach((item) => {
    const opt = document.createElement('option');
    opt.value = item.id;
    opt.textContent = item.nombre;
    select.appendChild(opt);
  });
  select.value = valorActual || '';
}

/* ---------- Tabla ---------- */
async function cargarProductos() {
  const params = new URLSearchParams();
  if (inputBuscar.value.trim()) params.set('nombre', inputBuscar.value.trim());
  if (filtroProveedor.value) params.set('proveedorId', filtroProveedor.value);
  if (filtroTipo.value) params.set('tipoAlmacenId', filtroTipo.value);

  try {
    const productos = await api.get('/productos?' + params.toString());
    renderTabla(productos);
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function renderTabla(productos) {
  tablaBody.innerHTML = '';
  estadoVacio.classList.toggle('hidden', productos.length > 0);

  productos.forEach((p) => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${escapeHtml(p.nombre)}</td>
      <td class="num mono">${formatoNumero(p.cantidad)}</td>
      <td><span class="pill">${escapeHtml(p.tipoAlmacen?.nombre ?? '-')}</span></td>
      <td class="num mono">${formatoMoneda(p.precio)}</td>
      <td>${escapeHtml(p.proveedor?.nombre ?? '-')}</td>
      <td class="num mono">${formatoMoneda(p.total)}</td>
      <td>
        <div class="row-actions">
          <button class="btn btn-ghost btn-icon" title="Editar" data-accion="editar" data-id="${p.id}">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.1" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4Z"/></svg>
          </button>
          <button class="btn btn-ghost btn-icon" title="Eliminar" data-accion="eliminar" data-id="${p.id}">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.1" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/></svg>
          </button>
        </div>
      </td>`;
    tablaBody.appendChild(tr);
  });

  tablaBody.querySelectorAll('[data-accion="editar"]').forEach((btn) =>
    btn.addEventListener('click', () => abrirModal(btn.dataset.id)));
  tablaBody.querySelectorAll('[data-accion="eliminar"]').forEach((btn) =>
    btn.addEventListener('click', () => eliminarProducto(btn.dataset.id)));
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

/* ---------- Modal agregar / editar ---------- */
async function abrirModal(id) {
  formProducto.reset();
  limpiarErrores();
  document.getElementById('productoId').value = '';

  if (id) {
    modalTitulo.textContent = 'Editar producto';
    try {
      const p = await api.get('/productos/' + id);
      document.getElementById('productoId').value = p.id;
      document.getElementById('nombre').value = p.nombre;
      document.getElementById('cantidad').value = p.cantidad;
      document.getElementById('precio').value = p.precio;
      document.getElementById('tipoAlmacen').value = p.tipoAlmacen?.id ?? '';
      document.getElementById('proveedor').value = p.proveedor?.id ?? '';
    } catch (err) {
      showToast(err.message, 'error');
      return;
    }
  } else {
    modalTitulo.textContent = 'Agregar producto';
  }

  modalOverlay.classList.add('open');
}

function cerrarModal() {
  modalOverlay.classList.remove('open');
}

function limpiarErrores() {
  ['Nombre', 'Cantidad', 'Tipo', 'Precio', 'Proveedor'].forEach((campo) => {
    const el = document.getElementById('err' + campo);
    if (el) el.textContent = '';
  });
}

async function guardarProducto(e) {
  e.preventDefault();
  limpiarErrores();

  const id = document.getElementById('productoId').value;
  const payload = {
    nombre: document.getElementById('nombre').value.trim(),
    cantidad: Number(document.getElementById('cantidad').value),
    precio: Number(document.getElementById('precio').value),
    tipoAlmacen: { id: Number(document.getElementById('tipoAlmacen').value) },
    proveedor: { id: Number(document.getElementById('proveedor').value) },
  };

  try {
    if (id) {
      await api.put('/productos/' + id, payload);
      showToast('Producto actualizado correctamente', 'ok');
    } else {
      await api.post('/productos', payload);
      showToast('Producto agregado correctamente', 'ok');
    }
    cerrarModal();
    cargarProductos();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function eliminarProducto(id) {
  if (!confirm('¿Eliminar este producto del inventario? Esta accion no se puede deshacer.')) return;
  try {
    await api.del('/productos/' + id);
    showToast('Producto eliminado', 'ok');
    cargarProductos();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

document.addEventListener('DOMContentLoaded', init);
