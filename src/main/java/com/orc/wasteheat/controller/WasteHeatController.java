package com.orc.wasteheat.controller;

import com.orc.wasteheat.dto.WasteHeatRequest;
import com.orc.wasteheat.dto.WasteHeatResponse;
import com.orc.wasteheat.entity.WasteHeatData;
import com.orc.wasteheat.service.WasteHeatService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/waste-heat")
@CrossOrigin
public class WasteHeatController {

    private final WasteHeatService service;

    public WasteHeatController(WasteHeatService service) {
        this.service = service;
    }

    @GetMapping
    public List<WasteHeatResponse> getAll(
            @RequestParam(required = false) Long industrialUnitId,
            @RequestParam(required = false) WasteHeatData.HeatSource heatSource,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<WasteHeatData> data;
        if (industrialUnitId != null || heatSource != null || startDate != null || endDate != null) {
            data = service.filter(industrialUnitId, heatSource, startDate, endDate);
        } else {
            data = service.getAll();
        }
        return data.stream().map(WasteHeatResponse::from).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public WasteHeatResponse getById(@PathVariable Long id) {
        return WasteHeatResponse.from(service.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WasteHeatResponse create(@Valid @RequestBody WasteHeatRequest request) {
        return WasteHeatResponse.from(service.create(request));
    }

    @PutMapping("/{id}")
    public WasteHeatResponse update(@PathVariable Long id, @Valid @RequestBody WasteHeatRequest request) {
        return WasteHeatResponse.from(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
