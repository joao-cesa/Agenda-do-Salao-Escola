package com.salaoescola.agenda.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.salaoescola.agenda.dto.AgendamentoRequestDTO;
import com.salaoescola.agenda.dto.AgendamentoResponseDTO;
import com.salaoescola.agenda.service.AgendamentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> criar(@Valid @RequestBody AgendamentoRequestDTO requisicao) {
        AgendamentoResponseDTO criado = agendamentoService.criar(requisicao);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @GetMapping
    public List<AgendamentoResponseDTO> listar() {
        return agendamentoService.listar();
    }

    @GetMapping("/{id}")
    public AgendamentoResponseDTO buscar(@PathVariable Long id) {
        return agendamentoService.buscar(id);
    }

    @PatchMapping("/{id}/cancelar")
    public AgendamentoResponseDTO cancelar(@PathVariable Long id) {
        return agendamentoService.cancelar(id);
    }
}
