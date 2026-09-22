package com.ChaveMestra.Application.controller;

import com.ChaveMestra.Application.dto.MovimentoFinanceiroRequest;
import com.ChaveMestra.Application.dto.MovimentoFinanceiroResponse;
import com.ChaveMestra.Application.service.MovimentoFinanceiroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimentos-financeiros")
public class MovimentoFinanceiroController {

    private final MovimentoFinanceiroService movimentoService;

    public MovimentoFinanceiroController(MovimentoFinanceiroService movimentoService) {
        this.movimentoService = movimentoService;
    }

    @PostMapping
    public ResponseEntity<MovimentoFinanceiroResponse> cadastrar(
            @Valid @RequestBody MovimentoFinanceiroRequest request) {
        MovimentoFinanceiroResponse salvo = movimentoService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<MovimentoFinanceiroResponse>> listar() {
        return ResponseEntity.ok(movimentoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentoFinanceiroResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(movimentoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimentoFinanceiroResponse> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MovimentoFinanceiroRequest request) {
        return ResponseEntity.ok(movimentoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        movimentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}