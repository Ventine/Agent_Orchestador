package com.datacancha.agent.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "match_analysis")
public class MatchAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fixture_id", nullable = false)
    private Long fixtureId;

    @Column(name = "team_name")
    private String teamName;

    private String ballPossession;
    private Integer totalShots;

    // Control de estado de nuestro Pipeline (Extracción -> Guion -> Audio -> Video)
    @Column(name = "pipeline_status")
    private String pipelineStatus; 

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.pipelineStatus == null) {
            this.pipelineStatus = "DATA_EXTRACTED"; // Estado inicial
        }
    }
}