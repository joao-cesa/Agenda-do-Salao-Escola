package com.salaoescola.agenda.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload de POST /agendamentos. O horário final não é enviado: é calculado no servidor
 * (início + duração do serviço).
 */
public record AgendamentoRequestDTO(
        @NotBlank(message = "O nome do cliente é obrigatório.")
        @Size(max = 100, message = "O nome do cliente deve ter no máximo 100 caracteres.")
        String cliente,

        @NotNull(message = "O serviço é obrigatório.")
        Long idServico,

        @NotNull(message = "A profissional é obrigatória.")
        Long idProfissional,

        @NotNull(message = "A data/hora de início é obrigatória.")
        LocalDateTime dataHoraInicio) {
}
