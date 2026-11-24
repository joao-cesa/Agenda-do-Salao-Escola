package com.salaoescola.agenda.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.salaoescola.agenda.model.Agendamento;

/**
 * Consultas JPQL parametrizadas (sem concatenar texto do cliente). O join fetch carrega serviço e
 * profissional na mesma consulta, evitando N+1 ao montar a resposta.
 */
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("""
            select a from Agendamento a
            join fetch a.servico
            join fetch a.profissional
            order by a.dataHoraInicio, a.id
            """)
    List<Agendamento> listarTodos();

    @Query("""
            select a from Agendamento a
            join fetch a.servico
            join fetch a.profissional
            where a.id = :id
            """)
    Optional<Agendamento> buscarPorId(@Param("id") Long id);
}
