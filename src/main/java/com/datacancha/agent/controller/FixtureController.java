package com.datacancha.agent.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.datacancha.agent.dto.Santafe_DTOS.FixtureDto;
import com.datacancha.agent.service.SantaFeFixtureService;

@RestController
@RequestMapping("/api/v1/santafe")
public class FixtureController {

    private final SantaFeFixtureService fixtureService;

    public FixtureController(SantaFeFixtureService fixtureService) {
        this.fixtureService = fixtureService;
    }

    @GetMapping("/fixtures")
    public ResponseEntity<List<FixtureDto>> getFixtures(
            @RequestParam(defaultValue = "proximos") String status) {
        
        // Validación estricta
        if (!status.equalsIgnoreCase("proximos") && !status.equalsIgnoreCase("resultados")) {
            throw new IllegalArgumentException("El parámetro status debe ser 'proximos' o 'resultados'.");
        }

        List<FixtureDto> fixtures = fixtureService.getFixtures(status.toLowerCase());
        return ResponseEntity.ok(fixtures);
    }
} 