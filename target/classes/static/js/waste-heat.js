requireLogin();

let allRecords = [];
let allUnitsCache = [];

async function loadUnitsIntoSelectors() {
  try {
    allUnitsCache = await api.get('/industrial-units');
    const unitFilter = document.getElementById('unitFilter');
    const unitSelect = document.getElementById('industrialUnitId');
    const options = allUnitsCache.map(u => `<option value="${u.id}">${escapeHtml(u.industryName)}</option>`).join('');
    unitFilter.innerHTML = '<option value="">All Industrial Units</option>' + options;
    unitSelect.innerHTML = options || '<option value="">No industrial units yet</option>';
  } catch (err) {
    showToast(err.message, true);
  }
}

async function loadRecords() {
  const unitId = document.getElementById('unitFilter').value;
  const source = document.getElementById('sourceFilter').value;
  const params = new URLSearchParams();
  if (unitId) params.set('industrialUnitId', unitId);
  if (source) params.set('heatSource', source);
  const query = params.toString() ? `?${params.toString()}` : '';

  try {
    allRecords = await api.get(`/waste-heat${query}`);
    renderTable(allRecords);
  } catch (err) {
    showToast(err.message, true);
    document.getElementById('recordsBody').innerHTML = `<tr><td colspan="9" class="muted">Failed to load: ${err.message}</td></tr>`;
  }
}

function renderTable(records) {
  const body = document.getElementById('recordsBody');
  if (!records.length) {
    body.innerHTML = '<tr><td colspan="9" class="muted">No waste heat measurements recorded yet.</td></tr>';
    return;
  }
  body.innerHTML = records.map(r => {
    const deltaT = (r.inletTemperature - r.outletTemperature).toFixed(1);
    const perf = r.performance || {};
    return `
      <tr>
        <td>${escapeHtml(r.industryName || '-')}</td>
        <td>${HEAT_SOURCE_LABELS[r.heatSource] || r.heatSource}</td>
        <td>${r.recordedDate || '-'}</td>
        <td>${formatNumber(r.flowRate, 2)}</td>
        <td>${deltaT}</td>
        <td>${perf.recoverableHeatKw != null ? formatNumber(perf.recoverableHeatKw) : '-'}</td>
        <td>${perf.electricalPowerKw != null ? formatNumber(perf.electricalPowerKw) : '-'}</td>
        <td>${perf.dailyEnergyKwh != null ? formatNumber(perf.dailyEnergyKwh) : '-'}</td>
        <td>
          <button class="btn btn-outline btn-sm" onclick="editRecord(${r.id})">Edit</button>
          <button class="btn btn-danger btn-sm" onclick="deleteRecord(${r.id})">Delete</button>
        </td>
      </tr>`;
  }).join('');
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

function openModal(title) {
  document.getElementById('modalTitle').textContent = title;
  document.getElementById('recordModal').classList.add('open');
}
function closeModal() {
  document.getElementById('recordModal').classList.remove('open');
  document.getElementById('recordForm').reset();
  document.getElementById('recordId').value = '';
}

document.getElementById('addRecordBtn').addEventListener('click', () => {
  if (!allUnitsCache.length) {
    showToast('Please add an industrial unit first', true);
    return;
  }
  openModal('Add Waste Heat Measurement');
});
document.getElementById('cancelModal').addEventListener('click', closeModal);
document.getElementById('unitFilter').addEventListener('change', loadRecords);
document.getElementById('sourceFilter').addEventListener('change', loadRecords);
document.getElementById('clearFilters').addEventListener('click', () => {
  document.getElementById('unitFilter').value = '';
  document.getElementById('sourceFilter').value = '';
  loadRecords();
});

window.editRecord = function (id) {
  const r = allRecords.find(x => x.id === id);
  if (!r) return;
  document.getElementById('recordId').value = r.id;
  document.getElementById('industrialUnitId').value = r.industrialUnitId;
  document.getElementById('heatSource').value = r.heatSource;
  document.getElementById('flowRate').value = r.flowRate;
  document.getElementById('inletTemperature').value = r.inletTemperature;
  document.getElementById('outletTemperature').value = r.outletTemperature;
  document.getElementById('specificHeat').value = r.specificHeat;
  document.getElementById('heatRecoveryFactor').value = r.heatRecoveryFactor;
  document.getElementById('operatingHours').value = r.operatingHours;
  document.getElementById('operatingDays').value = r.operatingDays;
  document.getElementById('orcEfficiency').value = r.performance ? r.performance.orcEfficiency : 18;
  document.getElementById('gridEmissionFactor').value = 0.7;
  document.getElementById('recordedDate').value = r.recordedDate || '';
  openModal('Edit Waste Heat Measurement');
};

window.deleteRecord = async function (id) {
  if (!confirm('Delete this waste heat measurement?')) return;
  try {
    await api.del(`/waste-heat/${id}`);
    showToast('Measurement deleted');
    loadRecords();
  } catch (err) {
    showToast(err.message, true);
  }
};

document.getElementById('recordForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const id = document.getElementById('recordId').value;

  const inlet = parseFloat(document.getElementById('inletTemperature').value);
  const outlet = parseFloat(document.getElementById('outletTemperature').value);
  if (inlet <= outlet) {
    showToast('Inlet temperature must be greater than outlet temperature', true);
    return;
  }

  const payload = {
    industrialUnitId: parseInt(document.getElementById('industrialUnitId').value, 10),
    heatSource: document.getElementById('heatSource').value,
    flowRate: parseFloat(document.getElementById('flowRate').value),
    inletTemperature: inlet,
    outletTemperature: outlet,
    specificHeat: parseFloat(document.getElementById('specificHeat').value),
    heatRecoveryFactor: parseFloat(document.getElementById('heatRecoveryFactor').value),
    orcEfficiency: parseFloat(document.getElementById('orcEfficiency').value),
    operatingHours: parseInt(document.getElementById('operatingHours').value, 10),
    operatingDays: parseInt(document.getElementById('operatingDays').value, 10),
    gridEmissionFactor: parseFloat(document.getElementById('gridEmissionFactor').value),
    recordedDate: document.getElementById('recordedDate').value || null
  };

  try {
    if (id) {
      await api.put(`/waste-heat/${id}`, payload);
      showToast('Measurement updated and recalculated');
    } else {
      await api.post('/waste-heat', payload);
      showToast('Measurement saved and calculated');
    }
    closeModal();
    loadRecords();
  } catch (err) {
    showToast(err.message, true);
  }
});

(async function init() {
  await loadUnitsIntoSelectors();
  await loadRecords();
})();
