package com.datacancha.agent.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamDto {
    private Long id;
    private String name;
}