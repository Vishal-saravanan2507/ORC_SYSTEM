requireLogin();

document.getElementById('calcForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  const inlet = parseFloat(document.getElementById('inletTemperature').value);
  const outlet = parseFloat(document.getElementById('outletTemperature').value);
  if (inlet <= outlet) {
    showToast('Inlet temperature must be greater than outlet temperature', true);
    return;
  }

  const payload = {
    flowRate: parseFloat(document.getElementById('flowRate').value),
    inletTemperature: inlet,
    outletTemperature: outlet,
    specificHeat: parseFloat(document.getElementById('specificHeat').value),
    heatRecoveryFactor: parseFloat(document.getElementById('heatRecoveryFactor').value),
    orcEfficiency: parseFloat(document.getElementById('orcEfficiency').value),
    operatingHours: parseInt(document.getElementById('operatingHours').value, 10),
    operatingDays: parseInt(document.getElementById('operatingDays').value, 10),
    gridEmissionFactor: parseFloat(document.getElementById('gridEmissionFactor').value)
  };

  try {
    const result = await api.post('/orc/calculate', payload);
    document.getElementById('rHeat').textContent = formatNumber(result.recoverableHeatKw);
    document.getElementById('rPower').textContent = formatNumber(result.electricalPowerKw);
    document.getElementById('rDaily').textContent = formatNumber(result.dailyEnergyKwh);
    document.getElementById('rMonthly').textContent = formatNumber(result.monthlyEnergyKwh);
    document.getElementById('rAnnual').textContent = formatNumber(result.annualEnergyKwh);
    document.getElementById('rCO2Daily').textContent = formatNumber(result.dailyCO2ReductionKg);
    document.getElementById('rCO2Monthly').textContent = formatNumber(result.monthlyCO2ReductionTonnes, 3);
    document.getElementById('rCO2Annual').textContent = formatNumber(result.annualCO2ReductionTonnes, 3);

    document.getElementById('resultsGrid').style.display = 'grid';
    document.getElementById('resultsGrid2').style.display = 'grid';
    document.getElementById('placeholderMsg').style.display = 'none';
  } catch (err) {
    showToast(err.message, true);
  }
});
