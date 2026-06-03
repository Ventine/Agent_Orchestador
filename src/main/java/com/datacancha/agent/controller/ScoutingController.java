package com.datacancha.agent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datacancha.agent.entity.MatchAnalysis;
import com.datacancha.agent.service.ScoutingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/scouting")
@RequiredArgsConstructor
public class ScoutingController {

    private final ScoutingService scoutingService;

    // Endpoint de prueba: GET http://localhost:8080/api/v1/scouting/fetch/1032894
    @GetMapping("/fetch/{fixtureId}")
    public ResponseEntity<MatchAnalysis> fetchMatchData(@PathVariable Long fixtureId) {
        try {
            MatchAnalysis result = scoutingService.extractAndSaveStatistics(fixtureId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}