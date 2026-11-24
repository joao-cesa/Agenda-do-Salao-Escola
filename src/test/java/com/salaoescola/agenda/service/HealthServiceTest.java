package com.salaoescola.agenda.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.salaoescola.agenda.dto.HealthResponseDTO;

class HealthServiceTest {

    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final HealthService healthService = new HealthService(jdbcTemplate);

    @Test
    void retornaUpQuandoConsultaDeSaudeFunciona() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);

        HealthResponseDTO resposta = healthService.verificar();

        assertThat(resposta.estaSaudavel()).isTrue();
        assertThat(resposta.banco()).isEqualTo("UP");
        assertThat(resposta.iniciadoEm()).isNotNull();
    }

    @Test
    void retornaDownSemVazarDetalhesDoDriverQuandoBancoFalha() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenThrow(
                new DataAccessResourceFailureException("Connection refused: jdbc:postgresql://host-secreto/db user=admin"));

        HealthResponseDTO resposta = healthService.verificar();

        assertThat(resposta.estaSaudavel()).isFalse();
        assertThat(resposta.banco()).isEqualTo("DOWN");
        assertThat(resposta.mensagem()).doesNotContain("jdbc").doesNotContain("host-secreto").doesNotContain("admin");
    }

    @Test
    void iniciadoEmPermaneceIgualEntreChamadasDaMesmaInstancia() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);

        assertThat(healthService.verificar().iniciadoEm()).isEqualTo(healthService.verificar().iniciadoEm());
    }
}
