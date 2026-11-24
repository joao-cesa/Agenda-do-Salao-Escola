package com.salaoescola.agenda;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.salaoescola.agenda.model.Agendamento;
import com.salaoescola.agenda.model.Profissional;
import com.salaoescola.agenda.model.Servico;
import com.salaoescola.agenda.model.StatusAgendamento;
import com.salaoescola.agenda.repository.AgendamentoRepository;
import com.salaoescola.agenda.repository.ProfissionalRepository;
import com.salaoescola.agenda.repository.ServicoRepository;

/**
 * Testa criar, consultar e cancelar agendamentos de ponta a ponta (HTTP -> service -> JPA -> H2),
 * usando o schema e os dados iniciais reais de banco/. Dados fictícios apenas.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:persistencia-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
@AutoConfigureMockMvc
class AgendamentoPersistenciaTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Test
    void criaAgendamentoCalculaFimEPersisteNoBanco() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/agendamentos")
                        .contentType(APPLICATION_JSON)
                        .content(corpo("Cliente Teste 1", idServico("Corte"), idProfissional("Ana"), "2026-12-01T14:00:00")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/agendamentos/")))
                .andExpect(jsonPath("$.cliente").value("Cliente Teste 1"))
                .andExpect(jsonPath("$.servico").value("Corte"))
                .andExpect(jsonPath("$.profissional").value("Ana"))
                .andExpect(jsonPath("$.dataHoraInicio").value("2026-12-01T14:00:00"))
                .andExpect(jsonPath("$.dataHoraFim").value("2026-12-01T14:30:00"))
                .andExpect(jsonPath("$.status").value("ATIVO"))
                .andReturn();

        Long id = idDaResposta(resultado);
        Agendamento gravado = agendamentoRepository.findById(id).orElseThrow();
        assertThat(gravado.getStatus()).isEqualTo(StatusAgendamento.ATIVO);
    }

    @Test
    void consultaAgendamentoPorIdELista() throws Exception {
        Long id = criarAgendamento("Cliente Teste 2", "Escova", "Bruno", "2026-12-02T10:00:00");

        mockMvc.perform(get("/agendamentos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.dataHoraFim").value("2026-12-02T10:45:00"));

        mockMvc.perform(get("/agendamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(hasItem(id.intValue())));
    }

    @Test
    void cancelarMudaStatusEPreservaORegistro() throws Exception {
        Long id = criarAgendamento("Cliente Teste 3", "Corte", "Ana", "2026-12-03T09:00:00");

        mockMvc.perform(patch("/agendamentos/" + id + "/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));

        Agendamento preservado = agendamentoRepository.findById(id).orElseThrow();
        assertThat(preservado.getStatus()).isEqualTo(StatusAgendamento.CANCELADO);
    }

    @Test
    void cancelarDuasVezesRetorna409() throws Exception {
        Long id = criarAgendamento("Cliente Teste 4", "Corte", "Ana", "2026-12-04T09:00:00");
        mockMvc.perform(patch("/agendamentos/" + id + "/cancelar")).andExpect(status().isOk());

        mockMvc.perform(patch("/agendamentos/" + id + "/cancelar"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void servicoInexistenteRetorna400() throws Exception {
        mockMvc.perform(post("/agendamentos")
                        .contentType(APPLICATION_JSON)
                        .content(corpo("Cliente Teste 5", 999_999L, idProfissional("Ana"), "2026-12-01T14:00:00")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(containsString("Serviço não encontrado")));
    }

    @Test
    void clienteEmBrancoRetorna400() throws Exception {
        mockMvc.perform(post("/agendamentos")
                        .contentType(APPLICATION_JSON)
                        .content(corpo("  ", idServico("Corte"), idProfissional("Ana"), "2026-12-01T14:00:00")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalhes[0]").value(containsString("cliente")));
    }

    @Test
    void dataEmFormatoInvalidoRetorna400() throws Exception {
        mockMvc.perform(post("/agendamentos")
                        .contentType(APPLICATION_JSON)
                        .content(corpo("Cliente Teste 6", idServico("Corte"), idProfissional("Ana"), "amanhã às 14h")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agendamentoInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/agendamentos/999999")).andExpect(status().isNotFound());
        mockMvc.perform(patch("/agendamentos/999999/cancelar")).andExpect(status().isNotFound());
    }

    @Test
    void listaServicosEProfissionaisDosDadosIniciais() throws Exception {
        mockMvc.perform(get("/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nome == 'Escova')].duracaoMinutos").value(hasItem(45)));
        mockMvc.perform(get("/profissionais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome").value(hasItem("Ana")))
                .andExpect(jsonPath("$[*].nome").value(hasItem("Bruno")));
    }

    // ---- apoio ----

    private Long criarAgendamento(String cliente, String servico, String profissional, String inicio) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/agendamentos")
                        .contentType(APPLICATION_JSON)
                        .content(corpo(cliente, idServico(servico), idProfissional(profissional), inicio)))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andReturn();
        return idDaResposta(resultado);
    }

    private Long idDaResposta(MvcResult resultado) throws Exception {
        String json = resultado.getResponse().getContentAsString();
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"id\":(\\d+)").matcher(json);
        assertThat(m.find()).isTrue();
        return Long.valueOf(m.group(1));
    }

    private Long idServico(String nome) {
        return servicoRepository.findAll().stream()
                .filter(s -> s.getNome().equals(nome)).map(Servico::getId).findFirst().orElseThrow();
    }

    private Long idProfissional(String nome) {
        return profissionalRepository.findAll().stream()
                .filter(p -> p.getNome().equals(nome)).map(Profissional::getId).findFirst().orElseThrow();
    }

    private static String corpo(String cliente, Long idServico, Long idProfissional, String inicio) {
        return String.format(
                "{\"cliente\":\"%s\",\"idServico\":%d,\"idProfissional\":%d,\"dataHoraInicio\":\"%s\"}",
                cliente, idServico, idProfissional, inicio);
    }
}
