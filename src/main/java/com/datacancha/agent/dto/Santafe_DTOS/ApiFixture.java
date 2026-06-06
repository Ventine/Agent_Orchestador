package com.datacancha.agent.dto.Santafe_DTOS;

public record ApiFixture(
    Long id,
    String date,
    ApiVenue venue,
    ApiStatus status
) {}