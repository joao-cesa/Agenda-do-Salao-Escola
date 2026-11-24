package com.salaoescola.agenda.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.salaoescola.agenda.dto.HealthResponseDTO;
import com.salaoescola.agenda.service.HealthService;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    private static final Instant INICIO = Instant.parse("2026-11-24T12:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthService healthService;

    @Test
    void retorna200ComIniciadoEmQuandoBancoSaudavel() throws Exception {
        when(healthService.verificar()).thenReturn(HealthResponseDTO.saudavel(INICIO));

        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.banco").value("UP"))
                .andExpect(jsonPath("$.iniciado_em").value("2026-11-24T12:00:00Z"))
                .andExpect(jsonPath("$.mensagem").doesNotExist());
    }

    @Test
    void retorna503QuandoBancoIndisponivel() throws Exception {
        when(healthService.verificar()).thenReturn(HealthResponseDTO.indisponivel(INICIO));

        mockMvc.perform(get("/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.banco").value("DOWN"))
                .andExpect(jsonPath("$.iniciado_em").exists());
    }
}
