package com.datacancha.agent.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamStatisticsDto {
    private TeamDto team;
    private List<StatDetailDto> statistics;
}