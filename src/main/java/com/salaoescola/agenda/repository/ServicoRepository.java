package com.salaoescola.agenda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salaoescola.agenda.model.Servico;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findAllByOrderByNomeAsc();
}
