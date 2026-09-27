package com.orc.wasteheat.service;

import com.orc.wasteheat.dto.ORCCalculationRequest;
import com.orc.wasteheat.dto.ORCCalculationResponse;
import com.orc.wasteheat.dto.WasteHeatRequest;
import com.orc.wasteheat.entity.IndustrialUnit;
import com.orc.wasteheat.entity.ORCPerformance;
import com.orc.wasteheat.entity.WasteHeatData;
import com.orc.wasteheat.exception.ResourceNotFoundException;
import com.orc.wasteheat.repository.IndustrialUnitRepository;
import com.orc.wasteheat.repository.ORCPerformanceRepository;
import com.orc.wasteheat.repository.WasteHeatDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Handles waste-heat measurement CRUD. Every create/update automatically
 * runs the measurement through ORCCalculationService and persists the
 * resulting ORCPerformance record, so the database always has the
 * calculated figures alongside the raw measurement.
 */
@Service
public class WasteHeatService {

    private final WasteHeatDataRepository wasteHeatRepository;
    private final IndustrialUnitRepository industrialUnitRepository;
    private final ORCPerformanceRepository performanceRepository;
    private final ORCCalculationService calculationService;

    public WasteHeatService(WasteHeatDataRepository wasteHeatRepository,
                             IndustrialUnitRepository industrialUnitRepository,
                             ORCPerformanceRepository performanceRepository,
                             ORCCalculationService calculationService) {
        this.wasteHeatRepository = wasteHeatRepository;
        this.industrialUnitRepository = industrialUnitRepository;
        this.performanceRepository = performanceRepository;
        this.calculationService = calculationService;
    }

    public List<WasteHeatData> getAll() {
        return wasteHeatRepository.findAllByOrderByRecordedDateDesc();
    }

    public WasteHeatData getById(Long id) {
        return wasteHeatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waste heat record not found with id: " + id));
    }

    public List<WasteHeatData> getByIndustrialUnit(Long industrialUnitId) {
        return wasteHeatRepository.findByIndustrialUnitIdOrderByRecordedDateDesc(industrialUnitId);
    }

    public List<WasteHeatData> filter(Long industrialUnitId, WasteHeatData.HeatSource heatSource,
                                       LocalDate startDate, LocalDate endDate) {
        return wasteHeatRepository.filter(industrialUnitId, heatSource, startDate, endDate);
    }

    @Transactional
    public WasteHeatData create(WasteHeatRequest request) {
        IndustrialUnit unit = industrialUnitRepository.findById(request.getIndustrialUnitId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Industrial unit not found with id: " + request.getIndustrialUnitId()));

        WasteHeatData data = new WasteHeatData();
        applyRequest(data, request);
        data.setIndustrialUnit(unit);
        if (request.getRecordedDate() != null) {
            data.setRecordedDate(request.getRecordedDate());
        }
        WasteHeatData saved = wasteHeatRepository.save(data);

        ORCPerformance performance = computeAndBuildPerformance(request, saved);
        performanceRepository.save(performance);
        saved.setPerformance(performance);
        return saved;
    }

    @Transactional
    public WasteHeatData update(Long id, WasteHeatRequest request) {
        WasteHeatData existing = getById(id);

        if (!existing.getIndustrialUnit().getId().equals(request.getIndustrialUnitId())) {
            IndustrialUnit unit = industrialUnitRepository.findById(request.getIndustrialUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Industrial unit not found with id: " + request.getIndustrialUnitId()));
            existing.setIndustrialUnit(unit);
        }

        applyRequest(existing, request);
        if (request.getRecordedDate() != null) {
            existing.setRecordedDate(request.getRecordedDate());
        }
        WasteHeatData saved = wasteHeatRepository.save(existing);

        ORCPerformance performance = performanceRepository.findByWasteHeatDataId(saved.getId())
                .orElseGet(ORCPerformance::new);
        performance.setWasteHeatData(saved);
        ORCCalculationResponse calc = calculationService.calculate(toCalculationRequest(request));
        copyCalculation(performance, calc);
        performanceRepository.save(performance);
        saved.setPerformance(performance);
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        WasteHeatData existing = getById(id);
        wasteHeatRepository.delete(existing);
    }

    private void applyRequest(WasteHeatData data, WasteHeatRequest request) {
        data.setHeatSource(request.getHeatSource());
        data.setFlowRate(request.getFlowRate());
        data.setInletTemperature(request.getInletTemperature());
        data.setOutletTemperature(request.getOutletTemperature());
        data.setSpecificHeat(request.getSpecificHeat());
        data.setHeatRecoveryFactor(request.getHeatRecoveryFactor());
        data.setOperatingHours(request.getOperatingHours());
        data.setOperatingDays(request.getOperatingDays());
    }

    private ORCPerformance computeAndBuildPerformance(WasteHeatRequest request, WasteHeatData saved) {
        ORCCalculationResponse calc = calculationService.calculate(toCalculationRequest(request));
        ORCPerformance performance = new ORCPerformance();
        performance.setWasteHeatData(saved);
        copyCalculation(performance, calc);
        return performance;
    }

    private void copyCalculation(ORCPerformance performance, ORCCalculationResponse calc) {
        performance.setRecoverableHeatKw(calc.getRecoverableHeatKw());
        performance.setOrcEfficiency(calc.getOrcEfficiency());
        performance.setElectricalPowerKw(calc.getElectricalPowerKw());
        performance.setDailyEnergyKwh(calc.getDailyEnergyKwh());
        performance.setMonthlyEnergyKwh(calc.getMonthlyEnergyKwh());
        performance.setAnnualEnergyKwh(calc.getAnnualEnergyKwh());
        performance.setDailyCO2ReductionKg(calc.getDailyCO2ReductionKg());
        performance.setMonthlyCO2ReductionTonnes(calc.getMonthlyCO2ReductionTonnes());
        performance.setAnnualCO2ReductionTonnes(calc.getAnnualCO2ReductionTonnes());
    }

    private ORCCalculationRequest toCalculationRequest(WasteHeatRequest request) {
        ORCCalculationRequest calcReq = new ORCCalculationRequest();
        calcReq.setFlowRate(request.getFlowRate());
        calcReq.setInletTemperature(request.getInletTemperature());
        calcReq.setOutletTemperature(request.getOutletTemperature());
        calcReq.setSpecificHeat(request.getSpecificHeat());
        calcReq.setHeatRecoveryFactor(request.getHeatRecoveryFactor());
        calcReq.setOrcEfficiency(request.getOrcEfficiency());
        calcReq.setOperatingHours(request.getOperatingHours());
        calcReq.setOperatingDays(request.getOperatingDays());
        calcReq.setGridEmissionFactor(request.getGridEmissionFactor());
        return calcReq;
    }
}
