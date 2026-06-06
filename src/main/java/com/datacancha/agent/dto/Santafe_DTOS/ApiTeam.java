package com.datacancha.agent.dto.Santafe_DTOS;

public record ApiTeam(
    Long id,
    String name,
    String code,
    String country,
    int founded,
    String logo
) {}