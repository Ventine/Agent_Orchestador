package com.datacancha.agent.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.datacancha.agent.dto.Santafe_DTOS.ApiFixtureData;
import com.datacancha.agent.dto.Santafe_DTOS.ApiFixtureResponse;
import com.datacancha.agent.dto.Santafe_DTOS.ApiTeam;
import com.datacancha.agent.dto.Santafe_DTOS.FixtureDto;
import com.datacancha.agent.entity.ClubFixture;
import com.datacancha.agent.exception.ApiIntegrationException;
import com.datacancha.agent.repository.ClubFixtureRepository;

@Service
public class SantaFeFixtureService {

    private final RestClient footballApiClient;
    private final ClubFixtureRepository repository;
    private final Long targetTeamId;

    public SantaFeFixtureService(
            RestClient footballApiClient, 
            ClubFixtureRepository repository, 
            @Value("${api.football.target-team-id}") Long targetTeamId) {
        this.footballApiClient = footballApiClient;
        this.repository = repository;
        this.targetTeamId = targetTeamId;
    }

    // value = "fixtures" es el nombre del caché. 
    // key = "#status" asegura que se guarde un caché para "proximos" y otro para "resultados"
    @Cacheable(value = "fixtures", key = "#status")
    @Transactional
    public List<FixtureDto> getFixtures(String status) {
        // 1. Determinar el parámetro de la API basado en el status
        String queryParam = status.equalsIgnoreCase("proximos") ? "next=5" : "last=5";

        // 2. Consumir la API (Solo ocurre si el caché expiró)
        ApiFixtureResponse response = footballApiClient.get()
                .uri("/fixtures?team={id}&{queryParam}&timezone=America/Bogota", targetTeamId, queryParam)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new ApiIntegrationException("Error al consultar fixtures: " + res.getStatusCode());
                })
                .body(ApiFixtureResponse.class);

        if (response == null || response.response().isEmpty()) {
            return List.of(); // Retorna lista vacía si no hay datos
        }

        // 3. Procesar, guardar en DB y mapear a DTO
        return response.response().stream()
                .map(this::processAndSaveFixture)
                .toList();
    }

    private FixtureDto processAndSaveFixture(ApiFixtureData data) {
        boolean isHome = data.teams().home().id().equals(targetTeamId);
        
        ApiTeam opponent = isHome ? data.teams().away() : data.teams().home();
        Integer homeGoals = data.goals().home();
        Integer awayGoals = data.goals().away();

        // Limpiar el estado para tu frontend
        String statusShort = data.fixture().status().short_name(); // Necesita @JsonProperty("short") en el Record
        String matchStatus = statusShort.equals("FT") || statusShort.equals("PEN") ? "Finalizado" : "Próximo";
        String score = matchStatus.equals("Finalizado") ? homeGoals + " - " + awayGoals : "Por jugar";

        // Guardar o Actualizar en BD (Upsert)
        ClubFixture entity = ClubFixture.builder()
                .id(data.fixture().id())
                // Suponiendo formato ISO-8601 que devuelve la API
                .matchDate(LocalDateTime.parse(data.fixture().date().substring(0, 19))) 
                .competition(data.league().name())
                .stadium(data.fixture().venue().name() + ", " + data.fixture().venue().city())
                .isHome(isHome)
                .opponentName(opponent.name())
                .opponentLogoUrl(opponent.logo())
                .homeGoals(homeGoals)
                .awayGoals(awayGoals)
                .statusShort(statusShort)
                .build();
        
        repository.save(entity);

        // Mapear al DTO final
        return new FixtureDto(
                data.fixture().id(),
                data.fixture().date(),
                data.league().name(),
                entity.getStadium(),
                isHome,
                opponent.name(),
                opponent.logo(),
                matchStatus,
                score
        );
    }
}