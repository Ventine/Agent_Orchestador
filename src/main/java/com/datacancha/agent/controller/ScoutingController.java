package com.datacancha.agent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datacancha.agent.dto.ApiResponse;
import com.datacancha.agent.entity.MatchAnalysis;
import com.datacancha.agent.service.ScoutingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/scouting")
@RequiredArgsConstructor
public class ScoutingController {

    private final ScoutingService scoutingService;

    @GetMapping("/fetch/{fixtureId}")
    public ResponseEntity<ApiResponse<MatchAnalysis>> fetchMatchData(@PathVariable Long fixtureId) {
        MatchAnalysis result = scoutingService.extractAndSaveStatistics(fixtureId);
        
        // Devolvemos la respuesta envuelta en nuestro formato estándar
        return ResponseEntity.ok(new ApiResponse<>(
                true, 
                "Estadísticas extraídas y persistidas correctamente en Supabase.", 
                result
        ));
    }
}