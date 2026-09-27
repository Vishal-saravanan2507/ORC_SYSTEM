package com.orc.wasteheat.service;

import com.orc.wasteheat.dto.ReportResponse;
import com.orc.wasteheat.entity.IndustrialUnit;
import com.orc.wasteheat.entity.ORCPerformance;
import com.orc.wasteheat.entity.WasteHeatData;
import com.orc.wasteheat.exception.ResourceNotFoundException;
import com.orc.wasteheat.repository.IndustrialUnitRepository;
import com.orc.wasteheat.repository.WasteHeatDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final WasteHeatDataRepository wasteHeatDataRepository;
    private final IndustrialUnitRepository industrialUnitRepository;

    public ReportService(WasteHeatDataRepository wasteHeatDataRepository,
                          IndustrialUnitRepository industrialUnitRepository) {
        this.wasteHeatDataRepository = wasteHeatDataRepository;
        this.industrialUnitRepository = industrialUnitRepository;
    }

    public ReportResponse generate(Long industrialUnitId, LocalDate startDate, LocalDate endDate) {
        String industryName = "All Industrial Units";
        if (industrialUnitId != null) {
            IndustrialUnit unit = industrialUnitRepository.findById(industrialUnitId)
                    .orElseThrow(() -> new ResourceNotFoundException("Industrial unit not found with id: " + industrialUnitId));
            industryName = unit.getIndustryName();
        }

        List<WasteHeatData> records = wasteHeatDataRepository.filter(industrialUnitId, null, startDate, endDate);

        ReportResponse report = new ReportResponse();
        report.setIndustryName(industryName);
        report.setStartDate(startDate != null ? startDate.toString() : "-");
        report.setEndDate(endDate != null ? endDate.toString() : "-");
        report.setNumberOfRecords(records.size());

        if (records.isEmpty()) {
            return report;
        }

        double totalHeat = 0, totalElectricity = 0, totalEfficiency = 0, totalCO2 = 0, totalTemp = 0;
        double peak = Double.NEGATIVE_INFINITY, min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        int perfCount = 0;

        for (WasteHeatData record : records) {
            totalTemp += record.getInletTemperature();
            ORCPerformance perf = record.getPerformance();
            if (perf != null) {
                totalHeat += nz(perf.getRecoverableHeatKw());
                totalElectricity += nz(perf.getDailyEnergyKwh());
                totalEfficiency += nz(perf.getOrcEfficiency());
                totalCO2 += nz(perf.getMonthlyCO2ReductionTonnes());
                double power = nz(perf.getElectricalPowerKw());
                peak = Math.max(peak, power);
                min = Math.min(min, power);
                max = Math.max(max, power);
                perfCount++;
            }
        }

        report.setTotalHeatRecoveredKwh(round(totalHeat));
        report.setTotalElectricityGeneratedKwh(round(totalElectricity));
        report.setAverageOrcEfficiency(perfCount > 0 ? round(totalEfficiency / perfCount) : 0);
        report.setTotalCO2ReductionTonnes(round(totalCO2));
        report.setAverageTemperature(round(totalTemp / records.size()));
        report.setPeakPowerKw(perfCount > 0 ? round(peak) : 0);
        report.setMinimumPowerKw(perfCount > 0 ? round(min) : 0);
        report.setMaximumPowerKw(perfCount > 0 ? round(max) : 0);

        return report;
    }

    private double nz(Double v) {
        return v == null ? 0.0 : v;
    }

    private double round(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}
