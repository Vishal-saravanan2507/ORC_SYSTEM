package com.orc.wasteheat.repository;

import com.orc.wasteheat.entity.ORCPerformance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ORCPerformanceRepository extends JpaRepository<ORCPerformance, Long> {
    Optional<ORCPerformance> findByWasteHeatDataId(Long wasteHeatDataId);
}
