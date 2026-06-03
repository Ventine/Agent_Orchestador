package com.datacancha.agent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // Leemos las nuevas variables directas
    @Value("${apisports.key}")
    private String apiKey;

    @Value("${apisports.host}")
    private String apiHost;

    @Bean
    public RestClient footballApiClient() {
        return RestClient.builder()
                // El código añade el https:// por ti, por eso el host debe venir limpio
                .baseUrl("https://" + apiHost)
                // Usamos la cabecera directa de API-Sports, ignorando RapidAPI
                .defaultHeader("x-apisports-key", apiKey)
                .build();
    }
}