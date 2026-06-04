package com.datacancha.agent.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.datacancha.agent.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Atrapa nuestros errores de negocio conocidos (ej: "No hay datos para este partido")
    @ExceptionHandler(ScoutingException.class)
    public ResponseEntity<ApiResponse<Void>> handleScoutingException(ScoutingException ex) {
        log.warn("Excepción de Scouting: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Atrapa cualquier otro error fatal inesperado (ej: Base de datos caída)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Error crítico en el sistema: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error interno del servidor. Por favor revisa los logs.", null));
    }
}