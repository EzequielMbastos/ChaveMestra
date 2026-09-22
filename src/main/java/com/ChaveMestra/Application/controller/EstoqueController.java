package com.ChaveMestra.Application.controller;

import com.ChaveMestra.Application.dto.EstoqueRequest;
import com.ChaveMestra.Application.dto.EstoqueResponse;
import com.ChaveMestra.Application.service.EstoqueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoques")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @PostMapping
    public ResponseEntity<EstoqueResponse> cadastrar(@Valid @RequestBody EstoqueRequest request) {
        EstoqueResponse salvo = estoqueService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<EstoqueResponse>> listar() {
        return ResponseEntity.ok(estoqueService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(estoqueService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstoqueResponse> atualizar(@PathVariable Integer id,
                                                     @Valid @RequestBody EstoqueRequest request) {
        return ResponseEntity.ok(estoqueService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        estoqueService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}