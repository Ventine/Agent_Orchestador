package com.datacancha.agent.dto.Santafe_DTOS;

public record ApiFixtureData(
    ApiFixture fixture,
    ApiLeague league,
    ApiTeams teams,
    ApiGoals goals
) {}