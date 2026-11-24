package com.salaoescola.agenda.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salaoescola.agenda.dto.ProfissionalResponseDTO;
import com.salaoescola.agenda.dto.ServicoResponseDTO;
import com.salaoescola.agenda.service.CatalogoService;

@RestController
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/servicos")
    public List<ServicoResponseDTO> listarServicos() {
        return catalogoService.listarServicos();
    }

    @GetMapping("/profissionais")
    public List<ProfissionalResponseDTO> listarProfissionais() {
        return catalogoService.listarProfissionais();
    }
}
