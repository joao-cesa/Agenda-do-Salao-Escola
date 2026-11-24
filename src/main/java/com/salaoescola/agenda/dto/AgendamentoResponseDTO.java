package com.salaoescola.agenda.dto;

import java.time.LocalDateTime;

import com.salaoescola.agenda.model.Agendamento;
import com.salaoescola.agenda.model.StatusAgendamento;

public record AgendamentoResponseDTO(
        Long id,
        String cliente,
        Long idServico,
        String servico,
        Long idProfissional,
        String profissional,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        StatusAgendamento status) {

    public static AgendamentoResponseDTO de(Agendamento agendamento) {
        return new AgendamentoResponseDTO(
                agendamento.getId(),
                agendamento.getCliente(),
                agendamento.getServico().getId(),
                agendamento.getServico().getNome(),
                agendamento.getProfissional().getId(),
                agendamento.getProfissional().getNome(),
                agendamento.getDataHoraInicio(),
                agendamento.getDataHoraFim(),
                agendamento.getStatus());
    }
}
