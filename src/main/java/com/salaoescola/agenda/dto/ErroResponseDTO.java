package com.salaoescola.agenda.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Formato único de erro da API: nunca inclui detalhes internos (SQL, driver, stack trace). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponseDTO(int status, String erro, String mensagem, List<String> detalhes) {

    public static ErroResponseDTO de(int status, String erro, String mensagem) {
        return new ErroResponseDTO(status, erro, mensagem, null);
    }
}
