package com.salaoescola.agenda.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "duracao_minutos", nullable = false)
    private int duracaoMinutos;

    protected Servico() {
        // exigido pelo JPA
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }
}
