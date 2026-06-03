package com.datacancha.agent.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatDetailDto {
    private String type; 
    private Object value; 
}