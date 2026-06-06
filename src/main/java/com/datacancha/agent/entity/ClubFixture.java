package com.datacancha.agent.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "club_fixtures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubFixture {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Long id; // ID del partido en API-FOOTBALL

    @Column(name = "match_date")
    private LocalDateTime matchDate;

    private String competition;
    private String stadium;
    
    @Column(name = "is_home")
    private Boolean isHome;

    @Column(name = "opponent_name")
    private String opponentName;

    @Column(name = "opponent_logo")
    private String opponentLogoUrl;

    @Column(name = "home_goals")
    private Integer homeGoals;

    @Column(name = "away_goals")
    private Integer awayGoals;

    @Column(name = "status_short")
    private String statusShort; // Ej: "NS" (Not Started), "FT" (Full Time)
}