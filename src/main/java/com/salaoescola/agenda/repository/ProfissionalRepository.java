package com.salaoescola.agenda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salaoescola.agenda.model.Profissional;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {

    List<Profissional> findAllByOrderByNomeAsc();
}
