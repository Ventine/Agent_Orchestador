package com.datacancha.agent.entity;

import com.datacancha.agent.dto.Santafe_DTOS.VenueProfile;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "club_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder 
public class ClubProfile {

    // Usaremos el mismo ID de la API (1128) para mantener la consistencia
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String code;
    private String country;
    private Integer foundedYear;
    private String logoUrl;

    @Embedded
    private VenueProfile venue;
}
