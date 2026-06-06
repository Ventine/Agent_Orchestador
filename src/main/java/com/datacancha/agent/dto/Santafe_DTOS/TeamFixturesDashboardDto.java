package com.datacancha.agent.dto.Santafe_DTOS;

import java.util.List;

public record TeamFixturesDashboardDto(
    List<FixtureDto> ultimosResultados,
    List<FixtureDto> proximosPartidos
) {}