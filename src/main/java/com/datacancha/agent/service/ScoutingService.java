package com.datacancha.agent.service;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.datacancha.agent.dto.ApiSportsResponse;
import com.datacancha.agent.entity.MatchAnalysis;
import com.datacancha.agent.exception.ScoutingException;
import com.datacancha.agent.repository.MatchAnalysisRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoutingService {

    private final RestClient footballApiClient;
    private final MatchAnalysisRepository repository;

    // ==========================================
    // BASE DE CONOCIMIENTO: COMPETICIONES ÉLITE
    // ==========================================
    private static final Set<Long> ELITE_COMPETITIONS = Set.of(
            // Torneos Internacionales y Copas Mayores
            1L,   // World Cup
            2L,   // UEFA Champions League
            3L,   // UEFA Europa League
            4L,   // Euro Championship
            9L,   // Copa America
            13L,  // Copa Libertadores
            17L,  // Copa Sudamericana
            66L,  // Club Friendlies (Amistosos élite)
            
            // Top 15 Ligas del Planeta
            39L,  // Premier League (Inglaterra)
            140L, // La Liga (España)
            135L, // Serie A (Italia)
            78L,  // Bundesliga (Alemania)
            61L,  // Ligue 1 (Francia)
            71L,  // Serie A (Brasil)
            128L, // Primera División (Argentina)
            239L, // Primera A (Colombia)
            88L,  // Eredivisie (Países Bajos)
            94L,  // Primeira Liga (Portugal)
            144L, // J1 League (Japón)
            253L, // MLS (USA)
            262L, // Liga MX (México)
            119L, // Superliga (Turquía)
            14L   // Europa Conference League
    );

    /**
     * Algoritmo de Curación Diaria.
     * Descarga TODOS los partidos del día anterior y filtra localmente buscando la élite.
     */
    public void extractBestMatchOfTheDay() {
        String yesterday = LocalDate.now().minusDays(1).toString();
        log.info("Iniciando escaneo global de partidos del día: {}", yesterday);

        try {
            // 1. Una sola llamada HTTP para traer el catálogo mundial del día
            var response = footballApiClient.get()
                    .uri("/fixtures?date={date}", yesterday)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null || !response.has("response") || response.get("response").isEmpty()) {
                log.warn("El catálogo global de API-Sports para ayer está vacío.");
                return; 
            }

            JsonNode fixtures = response.get("response");
            Long selectedFixtureId = null;
            String matchTitle = "";

            // 2. Filtro en Memoria RAM (O(N) de complejidad, super rápido)
            for (JsonNode node : fixtures) {
                JsonNode leagueNode = node.get("league");
                Long leagueId = leagueNode.get("id").asLong();

                // ¿Pertenece a nuestra lista de Élite?
                if (ELITE_COMPETITIONS.contains(leagueId)) {
                    
                    // 3. Validación de estado: Asegurar que el partido terminó (FT, AET o PEN)
                    String status = node.get("fixture").get("status").get("short").asText();
                    if ("FT".equals(status) || "AET".equals(status) || "PEN".equals(status)) {
                        
                        selectedFixtureId = node.get("fixture").get("id").asLong();
                        String home = node.get("teams").get("home").get("name").asText();
                        String away = node.get("teams").get("away").get("name").asText();
                        String leagueName = leagueNode.get("name").asText();
                        
                        matchTitle = home + " vs " + away + " (" + leagueName + ")";
                        
                        // Rompemos el ciclo al encontrar el primer partido válido.
                        // API-Sports los ordena por relevancia de liga automáticamente.
                        break; 
                    }
                }
            }

            // 4. Decisión del Algoritmo
            if (selectedFixtureId == null) {
                log.warn("No se encontraron partidos finalizados en competiciones de élite el día de ayer.");
                return;
            }

            log.info("¡Partido de Élite encontrado para análisis! ID: {} - {}", selectedFixtureId, matchTitle);
            
            // 5. Invocamos la extracción de estadísticas precisas
            extractAndSaveStatistics(selectedFixtureId);

        } catch (Exception e) {
            log.error("Fallo algorítmico al buscar el mejor partido del día: {}", e.getMessage());
        }
    }

    /**
     * Extrae las estadísticas tácticas de un partido específico y las guarda en SQL.
     */
    public MatchAnalysis extractAndSaveStatistics(Long fixtureId) {
        log.info("Extrayendo telemetría avanzada para el fixture: {}", fixtureId);

        try {
            ApiSportsResponse response = footballApiClient.get()
                    .uri("/fixtures/statistics?fixture={id}", fixtureId)
                    .retrieve()
                    .body(ApiSportsResponse.class);

            if (response == null || response.getResponse() == null || response.getResponse().isEmpty()) {
                log.warn("La API no devolvió métricas avanzadas para el fixture: {}", fixtureId);
                throw new ScoutingException("Falta de cobertura estadística para el partido ID: " + fixtureId);
            }

            var teamStats = response.getResponse().get(0);
            
            MatchAnalysis analysis = new MatchAnalysis();
            analysis.setFixtureId(fixtureId);
            analysis.setTeamName(teamStats.getTeam().getName());

            teamStats.getStatistics().forEach(stat -> {
                if ("Ball Possession".equals(stat.getType())) {
                    analysis.setBallPossession(String.valueOf(stat.getValue()));
                } else if ("Total Shots".equals(stat.getType())) {
                    analysis.setTotalShots(stat.getValue() != null ? Integer.parseInt(String.valueOf(stat.getValue())) : 0);
                }
            });

            MatchAnalysis savedAnalysis = repository.save(analysis);
            log.info("✅ Análisis persistido exitosamente en la nube (ID: {})", savedAnalysis.getId());
            
            return savedAnalysis;

        } catch (RestClientException e) {
            log.error("Fallo de red en capa HTTP con API-Sports: {}", e.getMessage());
            throw new ScoutingException("Error de I/O extrayendo estadísticas", e);
        }
    }
}