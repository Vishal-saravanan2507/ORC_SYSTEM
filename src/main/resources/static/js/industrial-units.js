requireLogin();

let allUnits = [];

async function loadUnits() {
  const search = document.getElementById('searchInput').value.trim();
  const status = document.getElementById('statusFilter').value;
  const params = new URLSearchParams();
  if (search) params.set('search', search);
  if (status) params.set('status', status);
  const query = params.toString() ? `?${params.toString()}` : '';

  try {
    allUnits = await api.get(`/industrial-units${query}`);
    renderTable(allUnits);
  } catch (err) {
    showToast(err.message, true);
    document.getElementById('unitsBody').innerHTML = `<tr><td colspan="8" class="muted">Failed to load: ${err.message}</td></tr>`;
  }
}

function renderTable(units) {
  const body = document.getElementById('unitsBody');
  if (!units.length) {
    body.innerHTML = '<tr><td colspan="8" class="muted">No industrial units found.</td></tr>';
    return;
  }
  body.innerHTML = units.map(u => `
    <tr>
      <td>${escapeHtml(u.industryName)}</td>
      <td>${INDUSTRY_TYPE_LABELS[u.industryType] || u.industryType}</td>
      <td>${escapeHtml(u.location || '-')}</td>
      <td>${escapeHtml(u.contactPerson || '-')}</td>
      <td>${u.installedCapacity != null ? formatNumber(u.installedCapacity) : '-'}</td>
      <td><span class="badge ${u.status === 'ACTIVE' ? 'badge-active' : 'badge-inactive'}">${u.status}</span></td>
      <td>${u.createdDate || '-'}</td>
      <td>
        <button class="btn btn-outline btn-sm" onclick="editUnit(${u.id})">Edit</button>
        <button class="btn btn-danger btn-sm" onclick="deleteUnit(${u.id})">Delete</button>
      </td>
    </tr>
  `).join('');
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

function openModal(title) {
  document.getElementById('modalTitle').textContent = title;
  document.getElementById('unitModal').classList.add('open');
}
function closeModal() {
  document.getElementById('unitModal').classList.remove('open');
  document.getElementById('unitForm').reset();
  document.getElementById('unitId').value = '';
}

document.getElementById('addUnitBtn').addEventListener('click', () => {
  openModal('Add Industrial Unit');
});
document.getElementById('cancelModal').addEventListener('click', closeModal);
document.getElementById('searchInput').addEventListener('input', debounce(loadUnits, 350));
document.getElementById('statusFilter').addEventListener('change', loadUnits);
document.getElementById('clearFilters').addEventListener('click', () => {
  document.getElementById('searchInput').value = '';
  document.getElementById('statusFilter').value = '';
  loadUnits();
});

function debounce(fn, delay) {
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => fn(...args), delay);
  };
}

window.editUnit = function (id) {
  const unit = allUnits.find(u => u.id === id);
  if (!unit) return;
  document.getElementById('unitId').value = unit.id;
  document.getElementById('industryName').value = unit.industryName;
  document.getElementById('industryType').value = unit.industryType;
  document.getElementById('status').value = unit.status;
  document.getElementById('location').value = unit.location || '';
  document.getElementById('contactPerson').value = unit.contactPerson || '';
  document.getElementById('installedCapacity').value = unit.installedCapacity ?? '';
  openModal('Edit Industrial Unit');
};

window.deleteUnit = async function (id) {
  if (!confirm('Delete this industrial unit? This will also remove its waste heat records.')) return;
  try {
    await api.del(`/industrial-units/${id}`);
    showToast('Industrial unit deleted');
    loadUnits();
  } catch (err) {
    showToast(err.message, true);
  }
};

document.getElementById('unitForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const id = document.getElementById('unitId').value;
  const payload = {
    industryName: document.getElementById('industryName').value.trim(),
    industryType: document.getElementById('industryType').value,
    status: document.getElementById('status').value,
    location: document.getElementById('location').value.trim(),
    contactPerson: document.getElementById('contactPerson').value.trim(),
    installedCapacity: document.getElementById('installedCapacity').value
      ? parseFloat(document.getElementById('installedCapacity').value) : null
  };

  try {
    if (id) {
      await api.put(`/industrial-units/${id}`, payload);
      showToast('Industrial unit updated');
    } else {
      await api.post('/industrial-units', payload);
      showToast('Industrial unit added');
    }
    closeModal();
    loadUnits();
  } catch (err) {
    showToast(err.message, true);
  }
});

loadUnits();
