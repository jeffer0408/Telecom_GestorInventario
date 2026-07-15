/* =========================================================
   historial.js - listado de movimientos del inventario
   ========================================================= */

requireAuth();

const tablaHistorial = document.getElementById('tablaHistorial');
const estadoVacioHistorial = document.getElementById('estadoVacioHistorial');

const PILL_POR_TIPO = {
  ALTA: { clase: 'pill-alta', texto: 'Alta' },
  EDICION: { clase: 'pill-edicion', texto: 'Edicion' },
  BAJA: { clase: 'pill-baja', texto: 'Baja' },
};

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

async function cargarHistorial() {
  try {
    const movimientos = await api.get('/movimientos');
    renderHistorial(movimientos);
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function renderHistorial(movimientos) {
  tablaHistorial.innerHTML = '';
  estadoVacioHistorial.classList.toggle('hidden', movimientos.length > 0);

  movimientos.forEach((m) => {
    const info = PILL_POR_TIPO[m.tipo] || { clase: 'pill', texto: m.tipo };
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td class="mono">${formatoFecha(m.fecha)}</td>
      <td><span class="pill ${info.clase}">${info.texto}</span></td>
      <td>${escapeHtml(m.producto)}</td>
      <td class="num mono">${m.cantidad != null ? formatoNumero(m.cantidad) : '-'}</td>
      <td class="text-muted">${escapeHtml(m.detalle ?? '')}</td>`;
    tablaHistorial.appendChild(tr);
  });
}

document.addEventListener('DOMContentLoaded', cargarHistorial);
