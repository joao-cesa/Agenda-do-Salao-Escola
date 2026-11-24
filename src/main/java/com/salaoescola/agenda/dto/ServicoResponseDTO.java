package com.salaoescola.agenda.dto;

import com.salaoescola.agenda.model.Servico;

public record ServicoResponseDTO(Long id, String nome, int duracaoMinutos) {

    public static ServicoResponseDTO de(Servico servico) {
        return new ServicoResponseDTO(servico.getId(), servico.getNome(), servico.getDuracaoMinutos());
    }
}
