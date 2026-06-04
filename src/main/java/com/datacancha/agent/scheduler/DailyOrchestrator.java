package com.datacancha.agent.scheduler;

import com.datacancha.agent.service.ScoutingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyOrchestrator {

    private final ScoutingService scoutingService;

    // Se ejecuta de Lunes a Domingo a las 7:00:00 AM hora de Bogotá
    // Formato CRON: Segundo, Minuto, Hora, DíaMes, Mes, DíaSemana
    @Scheduled(cron = "0 0 7 * * *", zone = "America/Bogota")
    public void startDailyPipeline() {
        log.info("Despertando el sistema a las 7:00 AM. Fecha: {}", LocalDate.now());
        
        try {
            // Aquí llamaremos al nuevo método inteligente que crearemos en el paso 2
            scoutingService.extractBestMatchOfTheDay();
            log.info("Pipeline de recolección finalizado con éxito.");
        } catch (Exception e) {
            log.error("El pipeline falló durante su ejecución matutina: {}", e.getMessage());
        }
    }
}