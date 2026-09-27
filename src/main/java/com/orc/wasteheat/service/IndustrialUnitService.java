package com.orc.wasteheat.service;

import com.orc.wasteheat.entity.IndustrialUnit;
import com.orc.wasteheat.exception.ResourceNotFoundException;
import com.orc.wasteheat.repository.IndustrialUnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndustrialUnitService {

    private final IndustrialUnitRepository repository;

    public IndustrialUnitService(IndustrialUnitRepository repository) {
        this.repository = repository;
    }

    public List<IndustrialUnit> getAll() {
        return repository.findAll();
    }

    public IndustrialUnit getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Industrial unit not found with id: " + id));
    }

    public IndustrialUnit create(IndustrialUnit unit) {
        unit.setId(null);
        return repository.save(unit);
    }

    public IndustrialUnit update(Long id, IndustrialUnit updated) {
        IndustrialUnit existing = getById(id);
        existing.setIndustryName(updated.getIndustryName());
        existing.setIndustryType(updated.getIndustryType());
        existing.setLocation(updated.getLocation());
        existing.setContactPerson(updated.getContactPerson());
        existing.setInstalledCapacity(updated.getInstalledCapacity());
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }
        return repository.save(existing);
    }

    public void delete(Long id) {
        IndustrialUnit existing = getById(id);
        repository.delete(existing);
    }

    public List<IndustrialUnit> search(String query) {
        if (query == null || query.isBlank()) {
            return repository.findAll();
        }
        return repository.search(query);
    }

    public List<IndustrialUnit> filterByStatus(IndustrialUnit.Status status) {
        return repository.findByStatus(status);
    }

    public long countByStatus(IndustrialUnit.Status status) {
        return repository.countByStatus(status);
    }
}
