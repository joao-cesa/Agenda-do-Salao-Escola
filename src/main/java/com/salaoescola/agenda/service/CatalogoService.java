package com.salaoescola.agenda.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.salaoescola.agenda.dto.ProfissionalResponseDTO;
import com.salaoescola.agenda.dto.ServicoResponseDTO;
import com.salaoescola.agenda.repository.ProfissionalRepository;
import com.salaoescola.agenda.repository.ServicoRepository;

/** Consulta de serviços e profissionais carregados pelo script de dados iniciais. */
@Service
@Transactional(readOnly = true)
public class CatalogoService {

    private final ServicoRepository servicoRepository;
    private final ProfissionalRepository profissionalRepository;

    public CatalogoService(ServicoRepository servicoRepository, ProfissionalRepository profissionalRepository) {
        this.servicoRepository = servicoRepository;
        this.profissionalRepository = profissionalRepository;
    }

    public List<ServicoResponseDTO> listarServicos() {
        return servicoRepository.findAllByOrderByNomeAsc().stream().map(ServicoResponseDTO::de).toList();
    }

    public List<ProfissionalResponseDTO> listarProfissionais() {
        return profissionalRepository.findAllByOrderByNomeAsc().stream().map(ProfissionalResponseDTO::de).toList();
    }
}
