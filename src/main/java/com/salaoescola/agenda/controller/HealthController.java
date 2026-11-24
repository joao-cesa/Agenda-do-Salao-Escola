package com.salaoescola.agenda.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salaoescola.agenda.dto.HealthResponseDTO;
import com.salaoescola.agenda.service.HealthService;

@RestController
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponseDTO> health() {
        HealthResponseDTO resposta = healthService.verificar();
        HttpStatus status = resposta.estaSaudavel() ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).body(resposta);
    }
}
