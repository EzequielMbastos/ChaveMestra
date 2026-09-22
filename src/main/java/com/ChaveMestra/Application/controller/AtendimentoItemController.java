package com.ChaveMestra.Application.controller;

import com.ChaveMestra.Application.dto.AtendimentoItemRequest;
import com.ChaveMestra.Application.dto.AtendimentoItemResponse;
import com.ChaveMestra.Application.service.AtendimentoItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atendimento-itens")
public class AtendimentoItemController {

    private final AtendimentoItemService itemService;

    public AtendimentoItemController(AtendimentoItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<AtendimentoItemResponse> cadastrar(
            @Valid @RequestBody AtendimentoItemRequest request) {
        AtendimentoItemResponse salvo = itemService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<AtendimentoItemResponse>> listar() {
        return ResponseEntity.ok(itemService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoItemResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(itemService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtendimentoItemResponse> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody AtendimentoItemRequest request) {
        return ResponseEntity.ok(itemService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        itemService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}