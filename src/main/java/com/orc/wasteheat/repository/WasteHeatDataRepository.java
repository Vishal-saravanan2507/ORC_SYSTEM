package com.orc.wasteheat.repository;

import com.orc.wasteheat.entity.WasteHeatData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WasteHeatDataRepository extends JpaRepository<WasteHeatData, Long> {

    List<WasteHeatData> findByIndustrialUnitIdOrderByRecordedDateDesc(Long industrialUnitId);

    List<WasteHeatData> findByHeatSource(WasteHeatData.HeatSource heatSource);

    List<WasteHeatData> findAllByOrderByRecordedDateDesc();

    @Query("SELECT w FROM WasteHeatData w WHERE " +
           "(:industrialUnitId IS NULL OR w.industrialUnit.id = :industrialUnitId) AND " +
           "(:heatSource IS NULL OR w.heatSource = :heatSource) AND " +
           "(:startDate IS NULL OR w.recordedDate >= :startDate) AND " +
           "(:endDate IS NULL OR w.recordedDate <= :endDate) " +
           "ORDER BY w.recordedDate ASC")
    List<WasteHeatData> filter(@Param("industrialUnitId") Long industrialUnitId,
                                @Param("heatSource") WasteHeatData.HeatSource heatSource,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);
}
