package com.datacancha.agent.dto.Santafe_DTOS;
import java.util.List;

// --- DTOs para Mapear la API de Football ---

public record ApiFootballTeamResponse(
    List<ApiTeamData> response
) {}
