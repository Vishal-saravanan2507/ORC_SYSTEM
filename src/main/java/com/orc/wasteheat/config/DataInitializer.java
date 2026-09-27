package com.orc.wasteheat.config;

import com.orc.wasteheat.dto.ORCCalculationRequest;
import com.orc.wasteheat.dto.ORCCalculationResponse;
import com.orc.wasteheat.entity.IndustrialUnit;
import com.orc.wasteheat.entity.ORCPerformance;
import com.orc.wasteheat.entity.WasteHeatData;
import com.orc.wasteheat.repository.IndustrialUnitRepository;
import com.orc.wasteheat.repository.ORCPerformanceRepository;
import com.orc.wasteheat.repository.WasteHeatDataRepository;
import com.orc.wasteheat.service.ORCCalculationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Seeds the database with a handful of clearly-labelled SIMULATED sample
 * industries and waste-heat measurements, purely so the application has
 * something to display immediately after first setup. Only runs when the
 * industrial_units table is empty, so it never duplicates data or
 * overwrites anything the user has entered.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final IndustrialUnitRepository industrialUnitRepository;
    private final WasteHeatDataRepository wasteHeatDataRepository;
    private final ORCPerformanceRepository performanceRepository;
    private final ORCCalculationService calculationService;

    public DataInitializer(IndustrialUnitRepository industrialUnitRepository,
                            WasteHeatDataRepository wasteHeatDataRepository,
                            ORCPerformanceRepository performanceRepository,
                            ORCCalculationService calculationService) {
        this.industrialUnitRepository = industrialUnitRepository;
        this.wasteHeatDataRepository = wasteHeatDataRepository;
        this.performanceRepository = performanceRepository;
        this.calculationService = calculationService;
    }

    @Override
    public void run(String... args) {
        if (industrialUnitRepository.count() > 0) {
            return; // Sample data already present (or real data entered) - do not touch it.
        }

        IndustrialUnit steel = unit("ABC Steel Plant", IndustrialUnit.IndustryType.STEEL,
                "Jamshedpur, Jharkhand", "R. Kumar", 5000.0);
        IndustrialUnit cement = unit("Green Cement Works", IndustrialUnit.IndustryType.CEMENT,
                "Ariyalur, Tamil Nadu", "S. Meena", 3500.0);
        IndustrialUnit glass = unit("Industrial Glass Factory", IndustrialUnit.IndustryType.GLASS,
                "Firozabad, Uttar Pradesh", "A. Sharma", 2000.0);

        industrialUnitRepository.save(steel);
        industrialUnitRepository.save(cement);
        industrialUnitRepository.save(glass);

        seedRecord(steel, WasteHeatData.HeatSource.FURNACE, 10.0, 450.0, 200.0, 1.1, 70.0, 18.0, 24, 30, 0.7, LocalDate.now().minusDays(6));
        seedRecord(steel, WasteHeatData.HeatSource.EXHAUST_GAS, 8.5, 420.0, 190.0, 1.08, 65.0, 17.0, 22, 28, 0.7, LocalDate.now().minusDays(3));
        seedRecord(cement, WasteHeatData.HeatSource.KILN, 12.0, 380.0, 160.0, 1.05, 60.0, 16.0, 24, 30, 0.7, LocalDate.now().minusDays(5));
        seedRecord(cement, WasteHeatData.HeatSource.BOILER, 9.0, 300.0, 140.0, 1.02, 68.0, 15.0, 20, 26, 0.7, LocalDate.now().minusDays(1));
        seedRecord(glass, WasteHeatData.HeatSource.PROCESS_HEATER, 6.5, 350.0, 150.0, 1.1, 72.0, 19.0, 24, 29, 0.7, LocalDate.now());
    }

    private IndustrialUnit unit(String name, IndustrialUnit.IndustryType type, String location,
                                 String contact, double capacity) {
        IndustrialUnit u = new IndustrialUnit();
        u.setIndustryName(name);
        u.setIndustryType(type);
        u.setLocation(location);
        u.setContactPerson(contact);
        u.setInstalledCapacity(capacity);
        u.setStatus(IndustrialUnit.Status.ACTIVE);
        u.setCreatedDate(LocalDate.now());
        return u;
    }

    private void seedRecord(IndustrialUnit unit, WasteHeatData.HeatSource source,
                             double flowRate, double inlet, double outlet, double cp,
                             double recoveryFactor, double orcEfficiency,
                             int hours, int days, double emissionFactor, LocalDate date) {
        WasteHeatData data = new WasteHeatData();
        data.setIndustrialUnit(unit);
        data.setHeatSource(source);
        data.setFlowRate(flowRate);
        data.setInletTemperature(inlet);
        data.setOutletTemperature(outlet);
        data.setSpecificHeat(cp);
        data.setHeatRecoveryFactor(recoveryFactor);
        data.setOperatingHours(hours);
        data.setOperatingDays(days);
        data.setRecordedDate(date);
        WasteHeatData saved = wasteHeatDataRepository.save(data);

        ORCCalculationRequest req = new ORCCalculationRequest();
        req.setFlowRate(flowRate);
        req.setInletTemperature(inlet);
        req.setOutletTemperature(outlet);
        req.setSpecificHeat(cp);
        req.setHeatRecoveryFactor(recoveryFactor);
        req.setOrcEfficiency(orcEfficiency);
        req.setOperatingHours(hours);
        req.setOperatingDays(days);
        req.setGridEmissionFactor(emissionFactor);
        ORCCalculationResponse calc = calculationService.calculate(req);

        ORCPerformance performance = new ORCPerformance();
        performance.setWasteHeatData(saved);
        performance.setRecoverableHeatKw(calc.getRecoverableHeatKw());
        performance.setOrcEfficiency(calc.getOrcEfficiency());
        performance.setElectricalPowerKw(calc.getElectricalPowerKw());
        performance.setDailyEnergyKwh(calc.getDailyEnergyKwh());
        performance.setMonthlyEnergyKwh(calc.getMonthlyEnergyKwh());
        performance.setAnnualEnergyKwh(calc.getAnnualEnergyKwh());
        performance.setDailyCO2ReductionKg(calc.getDailyCO2ReductionKg());
        performance.setMonthlyCO2ReductionTonnes(calc.getMonthlyCO2ReductionTonnes());
        performance.setAnnualCO2ReductionTonnes(calc.getAnnualCO2ReductionTonnes());
        performanceRepository.save(performance);
    }
}
