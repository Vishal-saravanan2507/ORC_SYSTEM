/**
 * api.js
 * Shared helpers used by every page: base URL, fetch wrapper with error
 * handling, demo-auth guard, sidebar active-link highlighting, and
 * small UI utilities (toast, currency/number formatting).
 */

// Same-origin by default (frontend is served from Spring Boot on :8080).
// If you run the frontend separately (e.g. VS Code Live Server on :5500),
// this still works because CORS is enabled on the backend for localhost.
const API_BASE = window.location.origin.includes('8080')
  ? '/api'
  : 'http://localhost:8080/api';

/** Wraps fetch() and turns non-2xx responses into thrown Errors with the backend's message. */
async function apiRequest(path, options = {}) {
  const opts = Object.assign({
    headers: { 'Content-Type': 'application/json' }
  }, options);

  let response;
  try {
    response = await fetch(`${API_BASE}${path}`, opts);
  } catch (networkErr) {
    throw new Error('Cannot reach the backend at ' + API_BASE + '. Is the Spring Boot application running?');
  }

  if (response.status === 204) return null;

  const contentType = response.headers.get('content-type') || '';
  const body = contentType.includes('application/json') ? await response.json() : await response.text();

  if (!response.ok) {
    const message = (body && body.message) ? body.message : `Request failed (${response.status})`;
    throw new Error(message);
  }
  return body;
}

const api = {
  get: (path) => apiRequest(path, { method: 'GET' }),
  post: (path, data) => apiRequest(path, { method: 'POST', body: JSON.stringify(data) }),
  put: (path, data) => apiRequest(path, { method: 'PUT', body: JSON.stringify(data) }),
  del: (path) => apiRequest(path, { method: 'DELETE' })
};

/* ---------------- Demo auth guard ---------------- */
// This is a DEMO login only (see login page). It is NOT production security -
// it simply keeps someone from landing straight on internal pages without
// visiting the login screen first, using localStorage as a session flag.
function requireLogin() {
  if (localStorage.getItem('orc_demo_auth') !== 'true') {
    window.location.href = 'index.html';
  }
}

function logout() {
  localStorage.removeItem('orc_demo_auth');
  window.location.href = 'index.html';
}

/* ---------------- UI helpers ---------------- */
function showToast(message, isError = false) {
  let toast = document.getElementById('globalToast');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = 'globalToast';
    toast.className = 'toast';
    document.body.appendChild(toast);
  }
  toast.textContent = message;
  toast.className = 'toast show' + (isError ? ' error' : '');
  clearTimeout(toast._timer);
  toast._timer = setTimeout(() => toast.classList.remove('show'), 3500);
}

function formatNumber(value, decimals = 2) {
  if (value === null || value === undefined || isNaN(value)) return '-';
  return Number(value).toLocaleString(undefined, { minimumFractionDigits: decimals, maximumFractionDigits: decimals });
}

function highlightActiveNav() {
  const current = window.location.pathname.split('/').pop() || 'dashboard.html';
  document.querySelectorAll('.sidebar nav a').forEach(link => {
    if (link.getAttribute('href') === current) {
      link.classList.add('active');
    }
  });
}

function setTopbarDate() {
  const el = document.getElementById('topbarDate');
  if (el) {
    el.textContent = new Date().toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric' });
  }
}

function toggleSidebar() {
  document.querySelector('.sidebar').classList.toggle('open');
}

document.addEventListener('DOMContentLoaded', () => {
  highlightActiveNav();
  setTopbarDate();
  const toggle = document.getElementById('menuToggle');
  if (toggle) toggle.addEventListener('click', toggleSidebar);
  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) logoutBtn.addEventListener('click', logout);
});

const HEAT_SOURCE_LABELS = {
  FURNACE: 'Furnace', BOILER: 'Boiler', KILN: 'Kiln', EXHAUST_GAS: 'Exhaust Gas',
  GAS_TURBINE: 'Gas Turbine', ENGINE: 'Engine', PROCESS_HEATER: 'Process Heater', OTHER: 'Other'
};
const INDUSTRY_TYPE_LABELS = {
  STEEL: 'Steel', CEMENT: 'Cement', GLASS: 'Glass', CHEMICAL: 'Chemical', TEXTILE: 'Textile',
  PAPER: 'Paper', FOOD_PROCESSING: 'Food Processing', POWER_PLANT: 'Power Plant', OTHER: 'Other'
};
