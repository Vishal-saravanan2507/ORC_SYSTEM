requireLogin();

async function loadUnitSelect() {
  try {
    const units = await api.get('/industrial-units');
    const select = document.getElementById('unitSelect');
    select.innerHTML = '<option value="">All Industrial Units</option>' +
      units.map(u => `<option value="${u.id}">${escapeHtml(u.industryName)}</option>`).join('');
  } catch (err) {
    showToast(err.message, true);
  }
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

async function generateReport() {
  const unitId = document.getElementById('unitSelect').value;
  const startDate = document.getElementById('startDate').value;
  const endDate = document.getElementById('endDate').value;

  const params = new URLSearchParams();
  if (unitId) params.set('industrialUnitId', unitId);
  if (startDate) params.set('startDate', startDate);
  if (endDate) params.set('endDate', endDate);
  const query = params.toString() ? `?${params.toString()}` : '';

  try {
    const report = await api.get(`/reports${query}`);
    renderReport(report);
  } catch (err) {
    showToast(err.message, true);
  }
}

function renderReport(report) {
  const reportCard = document.getElementById('reportCard');
  const emptyState = document.getElementById('emptyState');

  if (!report.numberOfRecords || report.numberOfRecords === 0) {
    reportCard.style.display = 'none';
    emptyState.style.display = 'block';
    return;
  }

  emptyState.style.display = 'none';
  reportCard.style.display = 'block';

  document.getElementById('reportSubtitle').textContent =
    `${report.industryName} · ${report.startDate} to ${report.endDate}`;

  document.getElementById('rHeat').textContent = formatNumber(report.totalHeatRecoveredKwh);
  document.getElementById('rElectricity').textContent = formatNumber(report.totalElectricityGeneratedKwh);
  document.getElementById('rEfficiency').textContent = formatNumber(report.averageOrcEfficiency, 1) + '%';
  document.getElementById('rCO2').textContent = formatNumber(report.totalCO2ReductionTonnes, 3);

  document.getElementById('rAvgTemp').textContent = formatNumber(report.averageTemperature, 1);
  document.getElementById('rPeak').textContent = formatNumber(report.peakPowerKw);
  document.getElementById('rMin').textContent = formatNumber(report.minimumPowerKw);
  document.getElementById('rMax').textContent = formatNumber(report.maximumPowerKw);

  document.getElementById('reportRecordCount').textContent =
    `Based on ${report.numberOfRecords} measurement record(s).`;
}

document.getElementById('generateBtn').addEventListener('click', generateReport);
document.getElementById('printBtn').addEventListener('click', () => window.print());

(async function init() {
  await loadUnitSelect();
  // Default range: last 30 days, so a first-time user sees something immediately.
  const today = new Date();
  const past = new Date();
  past.setDate(today.getDate() - 30);
  document.getElementById('endDate').value = today.toISOString().slice(0, 10);
  document.getElementById('startDate').value = past.toISOString().slice(0, 10);
  await generateReport();
})();
