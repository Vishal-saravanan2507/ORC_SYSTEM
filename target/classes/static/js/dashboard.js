requireLogin();

let powerChart, energyChart, tempChart, effChart;

async function loadDashboard() {
  try {
    const data = await api.get('/dashboard');
    renderKpis(data);
    renderCharts(data);
    renderRecent(data.recentMeasurements || []);
  } catch (err) {
    showToast(err.message, true);
  }
}

function renderKpis(data) {
  document.getElementById('kpiUnits').textContent = data.totalIndustrialUnits ?? 0;
  document.getElementById('kpiHeat').textContent = formatNumber(data.totalWasteHeatRecoveredKw);
  document.getElementById('kpiPower').textContent = formatNumber(data.currentEstimatedPowerKw);
  document.getElementById('kpiDaily').textContent = formatNumber(data.dailyEnergyGeneratedKwh);
  document.getElementById('kpiMonthly').textContent = formatNumber(data.monthlyEnergyGeneratedKwh);
  document.getElementById('kpiAnnual').textContent = formatNumber(data.annualEnergyPotentialKwh);
  document.getElementById('kpiCO2').textContent = formatNumber(data.monthlyCO2ReductionTonnes);
  document.getElementById('kpiEfficiency').textContent = formatNumber(data.averageOrcEfficiency, 1) + '%';
}

function toChartData(points) {
  return {
    labels: (points || []).map(p => p.label),
    values: (points || []).map(p => p.value)
  };
}

function renderCharts(data) {
  const power = toChartData(data.powerTrend);
  const energy = toChartData(data.energyTrend);
  const temp = toChartData(data.temperatureTrend);
  const eff = toChartData(data.efficiencyTrend);

  powerChart = makeLineChart('powerChart', powerChart, power.labels, power.values, 'Power (kW)', '#0f5c4c');
  energyChart = makeLineChart('energyChart', energyChart, energy.labels, energy.values, 'Energy (kWh)', '#e08a2c');
  tempChart = makeLineChart('tempChart', tempChart, temp.labels, temp.values, 'Inlet Temperature (°C)', '#c0392b');
  effChart = makeLineChart('effChart', effChart, eff.labels, eff.values, 'Efficiency (%)', '#2b6cb0');
}

function makeLineChart(canvasId, existingChart, labels, values, label, color) {
  if (existingChart) existingChart.destroy();
  const ctx = document.getElementById(canvasId).getContext('2d');
  return new Chart(ctx, {
    type: 'line',
    data: {
      labels: labels.length ? labels : ['No data'],
      datasets: [{
        label,
        data: values.length ? values : [0],
        borderColor: color,
        backgroundColor: color + '22',
        tension: 0.3,
        fill: true,
        pointRadius: 3
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: { y: { beginAtZero: true } }
    }
  });
}

function renderRecent(list) {
  const body = document.getElementById('recentBody');
  if (!list.length) {
    body.innerHTML = '<tr><td colspan="4" class="muted">No measurements recorded yet.</td></tr>';
    return;
  }
  body.innerHTML = list.map(r => `
    <tr>
      <td>${r.industryName || '-'}</td>
      <td>${HEAT_SOURCE_LABELS[r.heatSource] || r.heatSource || '-'}</td>
      <td>${r.recordedDate || '-'}</td>
      <td>${r.electricalPowerKw != null ? formatNumber(r.electricalPowerKw) : '-'}</td>
    </tr>
  `).join('');
}

loadDashboard();
