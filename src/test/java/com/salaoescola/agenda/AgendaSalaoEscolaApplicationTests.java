package com.salaoescola.agenda;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Smoke test: a aplicação sobe lendo a configuração por propriedades (sem .env), executa
 * banco/schema.sql e banco/dados-iniciais.sql e deixa os dados fictícios obrigatórios disponíveis.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:smoke-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class AgendaSalaoEscolaApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextoSobeEExecutaSchemaEDadosIniciais() {
        Integer profissionais = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM profissionais WHERE nome IN ('Ana', 'Bruno')", Integer.class);
        Integer duracaoEscova = jdbcTemplate.queryForObject(
                "SELECT duracao_minutos FROM servicos WHERE nome = 'Escova'", Integer.class);

        assertThat(profissionais).isEqualTo(2);
        assertThat(duracaoEscova).isEqualTo(45);
    }
}
