package com.datacancha.agent.dto.Santafe_DTOS;

public record ApiVenue(
    Long id,
    String name,
    String address,
    String city,
    int capacity,
    String surface,
    String image
) {}