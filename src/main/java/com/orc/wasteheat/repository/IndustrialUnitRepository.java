package com.orc.wasteheat.repository;

import com.orc.wasteheat.entity.IndustrialUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IndustrialUnitRepository extends JpaRepository<IndustrialUnit, Long> {

    List<IndustrialUnit> findByStatus(IndustrialUnit.Status status);

    @Query("SELECT u FROM IndustrialUnit u WHERE " +
           "LOWER(u.industryName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(u.location) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<IndustrialUnit> search(@Param("q") String query);

    List<IndustrialUnit> findByIndustryType(IndustrialUnit.IndustryType industryType);

    long countByStatus(IndustrialUnit.Status status);
}
