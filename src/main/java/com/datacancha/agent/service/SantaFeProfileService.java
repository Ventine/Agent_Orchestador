package com.datacancha.agent.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpStatusCode;

import java.util.Optional;

import com.datacancha.agent.dto.Santafe_DTOS.*;
import com.datacancha.agent.entity.ClubProfile;
import com.datacancha.agent.exception.ApiIntegrationException;
import com.datacancha.agent.repository.ClubProfileRepository;
import com.datacancha.agent.exception.GlobalExceptionHandler.*;
import com.datacancha.agent.exception.ResourceNotFoundException;

@Service
public class SantaFeProfileService {

    private final ClubProfileRepository repository;
    private final RestClient footballApiClient;
    private static final Long SANTA_FE_ID = 1139L;

    public SantaFeProfileService(ClubProfileRepository repository, RestClient footballApiClient) {
        this.repository = repository;
        this.footballApiClient = footballApiClient;
    }

    @Transactional
    public ClubProfileDto getProfile() {
        // 1. Validar si ya existe en Supabase
        Optional<ClubProfile> existingProfile = repository.findById(SANTA_FE_ID);
        
        if (existingProfile.isPresent()) {
            return mapToDto(existingProfile.get());
        }

        // 2. Si no existe, consumir API-FOOTBALL
        ApiFootballTeamResponse externalResponse = footballApiClient.get()
                .uri("/teams?id={id}", SANTA_FE_ID)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ApiIntegrationException("Error de cliente al consumir API-FOOTBALL: " + response.getStatusCode());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new ApiIntegrationException("Error del proveedor de API-FOOTBALL: " + response.getStatusCode());
                })
                .body(ApiFootballTeamResponse.class);

        // 3. Validar respuesta externa
        if (externalResponse == null || externalResponse.response().isEmpty()) {
            throw new ResourceNotFoundException("No se encontró el perfil para el ID " + SANTA_FE_ID);
        }

        // 4. Mapear, Guardar en Supabase y Retornar
        ApiTeamData data = externalResponse.response().get(0);
        ClubProfile savedProfile = saveToDatabase(data);
        
        return mapToDto(savedProfile);
    }

    private ClubProfile saveToDatabase(ApiTeamData data) {
        ApiTeam team = data.team();
        ApiVenue venue = data.venue();

        VenueProfile venueProfile = VenueProfile.builder()
                .venueId(venue.id())
                .venueName(venue.name())
                .address(venue.address())
                .city(venue.city())
                .capacity(venue.capacity())
                .surface(venue.surface())
                .imageUrl(venue.image())
                .build();

        ClubProfile profile = ClubProfile.builder()
                .id(team.id())
                .name(team.name())
                .code(team.code())
                .country(team.country())
                .foundedYear(team.founded())
                .logoUrl(team.logo())
                .venue(venueProfile)
                .build();

        return repository.save(profile);
    }

    private ClubProfileDto mapToDto(ClubProfile entity) {
        return new ClubProfileDto(
                entity.getId(),
                entity.getName(),
                entity.getFoundedYear(),
                entity.getLogoUrl(),
                entity.getVenue().getVenueName(),
                entity.getVenue().getCapacity(),
                entity.getVenue().getImageUrl()
        );
    }
}