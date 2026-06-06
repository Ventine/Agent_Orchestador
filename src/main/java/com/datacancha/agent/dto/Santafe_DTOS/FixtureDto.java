package com.datacancha.agent.dto.Santafe_DTOS;

public record FixtureDto(
    Long fixtureId,
    String matchDate, // Formateado a hora de Bogotá
    String competition,
    String stadium,
    Boolean isHomeMatch,
    String opponentName,
    String opponentLogoUrl,
    String matchStatus, // "Próximo" o "Finalizado"
    String score // Ej: "2 - 1" o "Por jugar"
) {}