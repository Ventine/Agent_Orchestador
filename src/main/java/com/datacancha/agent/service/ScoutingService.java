package com.datacancha.agent.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.datacancha.agent.dto.ApiSportsResponse;
import com.datacancha.agent.entity.MatchAnalysis;
import com.datacancha.agent.repository.MatchAnalysisRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Anotación de Lombok para habilitar logs
@Service
@RequiredArgsConstructor
public class ScoutingService {

    private final RestClient footballApiClient;
    private final MatchAnalysisRepository repository;

    /**
     * Extrae las estadísticas de un partido específico y las guarda en SQL Server.
     * @param fixtureId El ID del partido (ej. 1032894)
     */
    public MatchAnalysis extractAndSaveStatistics(Long fixtureId) {
        log.info("Iniciando extracción de datos para el partido (fixture): {}", fixtureId);

        try {
            // 1. Llamada HTTP a la API externa
            ApiSportsResponse response = footballApiClient.get()
                    // Quitamos el /v3 inicial de la ruta
                    .uri("/fixtures/statistics?fixture={id}", fixtureId)
                    .retrieve()
                    .body(ApiSportsResponse.class);

            if (response == null || response.getResponse() == null || response.getResponse().isEmpty()) {
                log.warn("La API no devolvió datos para el fixture: {}", fixtureId);
                throw new RuntimeException("No hay datos disponibles para este partido.");
            }

            // 2. Procesamiento de los datos (Tomamos el primer equipo del array como ejemplo)
            var teamStats = response.getResponse().get(0);
            
            MatchAnalysis analysis = new MatchAnalysis();
            analysis.setFixtureId(fixtureId);
            analysis.setTeamName(teamStats.getTeam().getName());

            // 3. Filtrado de métricas clave iterando el array de estadísticas
            teamStats.getStatistics().forEach(stat -> {
                if ("Ball Possession".equals(stat.getType())) {
                    analysis.setBallPossession(String.valueOf(stat.getValue()));
                } else if ("Total Shots".equals(stat.getType())) {
                    analysis.setTotalShots(stat.getValue() != null ? Integer.parseInt(String.valueOf(stat.getValue())) : 0);
                }
            });

            // 4. Guardado en Base de Datos
            MatchAnalysis savedAnalysis = repository.save(analysis);
            log.info("Análisis guardado exitosamente con ID: {}", savedAnalysis.getId());
            
            return savedAnalysis;

        } catch (RestClientException e) {
            log.error("Error de conexión con API-Sports: {}", e.getMessage());
            throw new RuntimeException("Fallo en la comunicación con el proveedor de datos.", e);
        } catch (Exception e) {
            log.error("Error inesperado procesando el fixture {}: {}", fixtureId, e.getMessage());
            throw e;
        }
    }
}