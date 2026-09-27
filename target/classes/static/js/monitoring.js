requireLogin();

let powerChart, effChart;

async function loadUnitFilter() {
  try {
    const units = await api.get('/industrial-units');
    const select = document.getElementById('unitFilter');
    select.innerHTML = '<option value="">All Industrial Units</option>' +
      units.map(u => `<option value="${u.id}">${escapeHtml(u.industryName)}</option>`).join('');
  } catch (err) {
    showToast(err.message, true);
  }
}

async function loadMonitoring() {
  const unitId = document.getElementById('unitFilter').value;
  const source = document.getElementById('sourceFilter').value;
  const startDate = document.getElementById('startDate').value;
  const endDate = document.getElementById('endDate').value;

  const params = new URLSearchParams();
  if (unitId) params.set('industrialUnitId', unitId);
  if (source) params.set('heatSource', source);
  if (startDate) params.set('startDate', startDate);
  if (endDate) params.set('endDate', endDate);
  const query = params.toString() ? `?${params.toString()}` : '';

  try {
    const records = await api.get(`/waste-heat${query}`);
    renderTable(records);
    renderCharts(records);
  } catch (err) {
    showToast(err.message, true);
    document.getElementById('monitorBody').innerHTML = `<tr><td colspan="10" class="muted">Failed to load: ${err.message}</td></tr>`;
  }
}

function renderTable(records) {
  const body = document.getElementById('monitorBody');
  if (!records.length) {
    body.innerHTML = '<tr><td colspan="10" class="muted">No measurements match the selected filters.</td></tr>';
    return;
  }
  body.innerHTML = records.map(r => {
    const perf = r.performance || {};
    return `
      <tr>
        <td>${r.recordedDate || '-'}</td>
        <td>${escapeHtml(r.industryName || '-')}</td>
        <td>${HEAT_SOURCE_LABELS[r.heatSource] || r.heatSource}</td>
        <td>${formatNumber(r.flowRate, 2)}</td>
        <td>${formatNumber(r.inletTemperature, 1)} / ${formatNumber(r.outletTemperature, 1)}</td>
        <td>${perf.recoverableHeatKw != null ? formatNumber(perf.recoverableHeatKw) : '-'}</td>
        <td>${perf.electricalPowerKw != null ? formatNumber(perf.electricalPowerKw) : '-'}</td>
        <td>${perf.orcEfficiency != null ? formatNumber(perf.orcEfficiency, 1) : '-'}</td>
        <td>${perf.dailyEnergyKwh != null ? formatNumber(perf.dailyEnergyKwh) : '-'}</td>
        <td>${perf.monthlyCO2ReductionTonnes != null ? formatNumber(perf.monthlyCO2ReductionTonnes, 3) : '-'}</td>
      </tr>`;
  }).join('');
}

function renderCharts(records) {
  const sorted = [...records].sort((a, b) => (a.recordedDate || '').localeCompare(b.recordedDate || ''));
  const labels = sorted.map(r => r.recordedDate || '-');
  const power = sorted.map(r => r.performance ? r.performance.electricalPowerKw : 0);
  const eff = sorted.map(r => r.performance ? r.performance.orcEfficiency : 0);

  if (powerChart) powerChart.destroy();
  if (effChart) effChart.destroy();

  powerChart = new Chart(document.getElementById('powerChart').getContext('2d'), {
    type: 'line',
    data: { labels: labels.length ? labels : ['No data'], datasets: [{ label: 'Power (kW)', data: power.length ? power : [0], borderColor: '#0f5c4c', backgroundColor: '#0f5c4c22', fill: true, tension: 0.3 }] },
    options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } }
  });

  effChart = new Chart(document.getElementById('effChart').getContext('2d'), {
    type: 'line',
    data: { labels: labels.length ? labels : ['No data'], datasets: [{ label: 'Efficiency (%)', data: eff.length ? eff : [0], borderColor: '#2b6cb0', backgroundColor: '#2b6cb022', fill: true, tension: 0.3 }] },
    options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true, max: 100 } } }
  });
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

document.getElementById('applyFilters').addEventListener('click', loadMonitoring);
document.getElementById('clearFilters').addEventListener('click', () => {
  document.getElementById('unitFilter').value = '';
  document.getElementById('sourceFilter').value = '';
  document.getElementById('startDate').value = '';
  document.getElementById('endDate').value = '';
  loadMonitoring();
});

(async function init() {
  await loadUnitFilter();
  await loadMonitoring();
})();
