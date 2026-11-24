package com.salaoescola.agenda.service;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.salaoescola.agenda.dto.HealthResponseDTO;

@Service
public class HealthService {

    private static final Logger log = LoggerFactory.getLogger(HealthService.class);

    private final JdbcTemplate jdbcTemplate;

    /** Momento em que esta instância da aplicação subiu; muda a cada reinício (prova de persistência, #10). */
    private final Instant iniciadoEm = Instant.now();

    public HealthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public HealthResponseDTO verificar() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return HealthResponseDTO.saudavel(iniciadoEm);
        } catch (DataAccessException e) {
            // Só o tipo da falha vai para o log: a mensagem do driver pode conter host/usuário do banco.
            log.warn("Falha na verificação de saúde do banco: {}", e.getClass().getSimpleName());
            return HealthResponseDTO.indisponivel(iniciadoEm);
        }
    }
}
