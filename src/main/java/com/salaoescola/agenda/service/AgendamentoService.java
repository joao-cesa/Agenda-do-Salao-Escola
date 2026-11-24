package com.salaoescola.agenda.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.salaoescola.agenda.dto.AgendamentoRequestDTO;
import com.salaoescola.agenda.dto.AgendamentoResponseDTO;
import com.salaoescola.agenda.exception.EntradaInvalidaException;
import com.salaoescola.agenda.exception.EstadoInvalidoException;
import com.salaoescola.agenda.exception.RecursoNaoEncontradoException;
import com.salaoescola.agenda.model.Agendamento;
import com.salaoescola.agenda.model.Profissional;
import com.salaoescola.agenda.model.Servico;
import com.salaoescola.agenda.repository.AgendamentoRepository;
import com.salaoescola.agenda.repository.ProfissionalRepository;
import com.salaoescola.agenda.repository.ServicoRepository;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ServicoRepository servicoRepository;
    private final ProfissionalRepository profissionalRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              ServicoRepository servicoRepository,
                              ProfissionalRepository profissionalRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.servicoRepository = servicoRepository;
        this.profissionalRepository = profissionalRepository;
    }

    @Transactional
    public AgendamentoResponseDTO criar(AgendamentoRequestDTO requisicao) {
        Servico servico = servicoRepository.findById(requisicao.idServico())
                .orElseThrow(() -> new EntradaInvalidaException("Serviço não encontrado: " + requisicao.idServico()));
        Profissional profissional = profissionalRepository.findById(requisicao.idProfissional())
                .orElseThrow(() -> new EntradaInvalidaException(
                        "Profissional não encontrada: " + requisicao.idProfissional()));

        LocalDateTime inicio = requisicao.dataHoraInicio();
        LocalDateTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());

        // PONTOS DE EXTENSÃO (outras issues), sempre ANTES do save e dentro desta transação:
        //   #12 -> validar dia (seg-sáb) e expediente 08:00-20:00 com início e fim (EntradaInvalidaException -> 400);
        //   #13 -> checar sobreposição de agendamentos ATIVOS da mesma profissional (conflito -> 409).

        Agendamento salvo = agendamentoRepository.save(new Agendamento(
                requisicao.cliente().trim(), servico, profissional, inicio, fim));
        return AgendamentoResponseDTO.de(salvo);
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponseDTO> listar() {
        return agendamentoRepository.listarTodos().stream()
                .map(AgendamentoResponseDTO::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public AgendamentoResponseDTO buscar(Long id) {
        return AgendamentoResponseDTO.de(buscarOuFalhar(id));
    }

    /** Cancelamento lógico: só muda o status; o registro continua no banco como histórico. */
    @Transactional
    public AgendamentoResponseDTO cancelar(Long id) {
        Agendamento agendamento = buscarOuFalhar(id);
        if (agendamento.estaCancelado()) {
            throw new EstadoInvalidoException("O agendamento " + id + " já está cancelado.");
        }
        agendamento.cancelar();
        return AgendamentoResponseDTO.de(agendamento);
    }

    private Agendamento buscarOuFalhar(Long id) {
        return agendamentoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado: " + id));
    }
}
