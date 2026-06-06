package com.datacancha.agent.dto.Santafe_DTOS;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueProfile {
    @Column(name = "venue_id")
    private Long venueId;

    @Column(name = "venue_name")
    private String venueName;

    @Column(name = "venue_address")
    private String address;

    @Column(name = "venue_city")
    private String city;

    @Column(name = "venue_capacity")
    private Integer capacity;

    @Column(name = "venue_surface")
    private String surface;

    @Column(name = "venue_image_url")
    private String imageUrl;
}