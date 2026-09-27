package com.orc.wasteheat.service;

import com.orc.wasteheat.dto.DashboardResponse;
import com.orc.wasteheat.entity.IndustrialUnit;
import com.orc.wasteheat.entity.ORCPerformance;
import com.orc.wasteheat.entity.WasteHeatData;
import com.orc.wasteheat.repository.IndustrialUnitRepository;
import com.orc.wasteheat.repository.WasteHeatDataRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Builds the dashboard payload entirely from data currently stored in
 * MySQL — no hard-coded statistics.
 */
@Service
public class DashboardService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM");

    private final IndustrialUnitRepository industrialUnitRepository;
    private final WasteHeatDataRepository wasteHeatDataRepository;

    public DashboardService(IndustrialUnitRepository industrialUnitRepository,
                             WasteHeatDataRepository wasteHeatDataRepository) {
        this.industrialUnitRepository = industrialUnitRepository;
        this.wasteHeatDataRepository = wasteHeatDataRepository;
    }

    public DashboardResponse build() {
        DashboardResponse response = new DashboardResponse();

        List<IndustrialUnit> units = industrialUnitRepository.findAll();
        response.setTotalIndustrialUnits(units.size());
        response.setTotalActiveUnits(industrialUnitRepository.countByStatus(IndustrialUnit.Status.ACTIVE));

        List<WasteHeatData> records = wasteHeatDataRepository.findAll();
        records.sort(Comparator.comparing(WasteHeatData::getRecordedDate));

        double totalRecoverable = 0, totalPower = 0, totalDaily = 0, totalMonthly = 0, totalAnnual = 0;
        double totalMonthlyCO2 = 0, totalEfficiency = 0;
        int countWithPerformance = 0;

        List<DashboardResponse.ChartPoint> powerTrend = new ArrayList<>();
        List<DashboardResponse.ChartPoint> energyTrend = new ArrayList<>();
        List<DashboardResponse.ChartPoint> tempTrend = new ArrayList<>();
        List<DashboardResponse.ChartPoint> efficiencyTrend = new ArrayList<>();

        for (WasteHeatData record : records) {
            ORCPerformance perf = record.getPerformance();
            String label = record.getRecordedDate() != null ? record.getRecordedDate().format(DATE_FMT) : "-";

            if (perf != null) {
                totalRecoverable += nz(perf.getRecoverableHeatKw());
                totalPower += nz(perf.getElectricalPowerKw());
                totalDaily += nz(perf.getDailyEnergyKwh());
                totalMonthly += nz(perf.getMonthlyEnergyKwh());
                totalAnnual += nz(perf.getAnnualEnergyKwh());
                totalMonthlyCO2 += nz(perf.getMonthlyCO2ReductionTonnes());
                totalEfficiency += nz(perf.getOrcEfficiency());
                countWithPerformance++;

                powerTrend.add(new DashboardResponse.ChartPoint(label, perf.getElectricalPowerKw()));
                energyTrend.add(new DashboardResponse.ChartPoint(label, perf.getDailyEnergyKwh()));
                efficiencyTrend.add(new DashboardResponse.ChartPoint(label, perf.getOrcEfficiency()));
            }
            tempTrend.add(new DashboardResponse.ChartPoint(label, record.getInletTemperature()));
        }

        response.setTotalWasteHeatRecoveredKw(round(totalRecoverable));
        response.setCurrentEstimatedPowerKw(round(totalPower));
        response.setDailyEnergyGeneratedKwh(round(totalDaily));
        response.setMonthlyEnergyGeneratedKwh(round(totalMonthly));
        response.setAnnualEnergyPotentialKwh(round(totalAnnual));
        response.setMonthlyCO2ReductionTonnes(round(totalMonthlyCO2));
        response.setAverageOrcEfficiency(countWithPerformance > 0 ? round(totalEfficiency / countWithPerformance) : 0);

        response.setPowerTrend(powerTrend);
        response.setEnergyTrend(energyTrend);
        response.setTemperatureTrend(tempTrend);
        response.setEfficiencyTrend(efficiencyTrend);

        List<DashboardResponse.RecentMeasurement> recent = new ArrayList<>();
        List<WasteHeatData> recentRecords = wasteHeatDataRepository.findAllByOrderByRecordedDateDesc();
        for (int i = 0; i < Math.min(5, recentRecords.size()); i++) {
            WasteHeatData r = recentRecords.get(i);
            DashboardResponse.RecentMeasurement rm = new DashboardResponse.RecentMeasurement();
            rm.setId(r.getId());
            rm.setIndustryName(r.getIndustrialUnit() != null ? r.getIndustrialUnit().getIndustryName() : "-");
            rm.setHeatSource(r.getHeatSource() != null ? r.getHeatSource().name() : "-");
            rm.setRecordedDate(r.getRecordedDate() != null ? r.getRecordedDate().toString() : "-");
            rm.setElectricalPowerKw(r.getPerformance() != null ? r.getPerformance().getElectricalPowerKw() : null);
            recent.add(rm);
        }
        response.setRecentMeasurements(recent);

        return response;
    }

    private double nz(Double v) {
        return v == null ? 0.0 : v;
    }

    private double round(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}
