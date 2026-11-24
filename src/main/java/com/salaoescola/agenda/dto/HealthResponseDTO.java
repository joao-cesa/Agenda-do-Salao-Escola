package com.salaoescola.agenda.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Resposta de GET /health. Nunca carrega DATABASE_URL, senha nem mensagem interna do driver.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record HealthResponseDTO(
        String status,
        String banco,
        @JsonProperty("iniciado_em") Instant iniciadoEm,
        String mensagem) {

    private static final String UP = "UP";
    private static final String DOWN = "DOWN";

    public static HealthResponseDTO saudavel(Instant iniciadoEm) {
        return new HealthResponseDTO(UP, UP, iniciadoEm, null);
    }

    public static HealthResponseDTO indisponivel(Instant iniciadoEm) {
        return new HealthResponseDTO(DOWN, DOWN, iniciadoEm, "Banco de dados indisponível no momento.");
    }

    public boolean estaSaudavel() {
        return UP.equals(status);
    }
}
