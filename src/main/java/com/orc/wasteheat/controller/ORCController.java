package com.orc.wasteheat.controller;

import com.orc.wasteheat.dto.ORCCalculationRequest;
import com.orc.wasteheat.dto.ORCCalculationResponse;
import com.orc.wasteheat.service.ORCCalculationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orc")
@CrossOrigin
public class ORCController {

    private final ORCCalculationService calculationService;

    public ORCController(ORCCalculationService calculationService) {
        this.calculationService = calculationService;
    }

    /**
     * Stateless "what-if" calculator: pass any set of parameters and get back
     * the estimated ORC performance. Does not touch the database, used by
     * the ORC Calculator page for instant results.
     */
    @PostMapping("/calculate")
    public ORCCalculationResponse calculate(@Valid @RequestBody ORCCalculationRequest request) {
        return calculationService.calculate(request);
    }
}
