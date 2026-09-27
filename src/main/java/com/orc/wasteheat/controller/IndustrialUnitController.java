package com.orc.wasteheat.controller;

import com.orc.wasteheat.entity.IndustrialUnit;
import com.orc.wasteheat.service.IndustrialUnitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/industrial-units")
@CrossOrigin
public class IndustrialUnitController {

    private final IndustrialUnitService service;

    public IndustrialUnitController(IndustrialUnitService service) {
        this.service = service;
    }

    @GetMapping
    public List<IndustrialUnit> getAll(@RequestParam(required = false) String search,
                                        @RequestParam(required = false) IndustrialUnit.Status status) {
        if (status != null) {
            return service.filterByStatus(status);
        }
        if (search != null && !search.isBlank()) {
            return service.search(search);
        }
        return service.getAll();
    }

    @GetMapping("/{id}")
    public IndustrialUnit getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IndustrialUnit create(@Valid @RequestBody IndustrialUnit unit) {
        return service.create(unit);
    }

    @PutMapping("/{id}")
    public IndustrialUnit update(@PathVariable Long id, @Valid @RequestBody IndustrialUnit unit) {
        return service.update(id, unit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
