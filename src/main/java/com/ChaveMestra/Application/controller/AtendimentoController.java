package com.ChaveMestra.Application.controller;

import com.ChaveMestra.Application.dto.AtendimentoRequest;
import com.ChaveMestra.Application.dto.AtendimentoResponse;
import com.ChaveMestra.Application.service.AtendimentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atendimentos")
public class AtendimentoController {

    private final AtendimentoService atendimentoService;

    public AtendimentoController(AtendimentoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    @PostMapping
    public ResponseEntity<AtendimentoResponse> cadastrar(@Valid @RequestBody AtendimentoRequest request) {
        AtendimentoResponse salvo = atendimentoService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<AtendimentoResponse>> listar() {
        return ResponseEntity.ok(atendimentoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(atendimentoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtendimentoResponse> atualizar(@PathVariable Integer id,
                                                         @Valid @RequestBody AtendimentoRequest request) {
        return ResponseEntity.ok(atendimentoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        atendimentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}