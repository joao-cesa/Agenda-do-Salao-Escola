package com.salaoescola.agenda.dto;

import com.salaoescola.agenda.model.Profissional;

public record ProfissionalResponseDTO(Long id, String nome) {

    public static ProfissionalResponseDTO de(Profissional profissional) {
        return new ProfissionalResponseDTO(profissional.getId(), profissional.getNome());
    }
}
