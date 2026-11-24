package com.salaoescola.agenda.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profissional_id", nullable = false)
    private Profissional profissional;

    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim", nullable = false)
    private LocalDateTime dataHoraFim;

    // A coluna é VARCHAR com CHECK (schema.sql); força VARCHAR para o Hibernate não tratar como enum nativo do H2.
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private StatusAgendamento status = StatusAgendamento.ATIVO;

    protected Agendamento() {
        // exigido pelo JPA
    }

    public Agendamento(String cliente, Servico servico, Profissional profissional,
                       LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim) {
        this.cliente = cliente;
        this.servico = servico;
        this.profissional = profissional;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.status = StatusAgendamento.ATIVO;
    }

    /** Exclusão lógica: o registro permanece no banco e deixa de bloquear o horário. */
    public void cancelar() {
        this.status = StatusAgendamento.CANCELADO;
    }

    public boolean estaCancelado() {
        return status == StatusAgendamento.CANCELADO;
    }

    public Long getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public Servico getServico() {
        return servico;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public StatusAgendamento getStatus() {
        return status;
    }
}
