package com.datacancha.agent.dto.Santafe_DTOS;

public record ClubProfileDto(
    Long id,
    String name,
    Integer foundedYear,
    String logoUrl,
    String stadiumName,
    Integer stadiumCapacity,
    String stadiumImageUrl
) {}