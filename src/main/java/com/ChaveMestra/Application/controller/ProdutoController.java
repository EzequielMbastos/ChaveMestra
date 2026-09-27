package com.ChaveMestra.Application.controller;

import com.ChaveMestra.Application.dto.ProdutoRequest;
import com.ChaveMestra.Application.dto.ProdutoResponse;
import com.ChaveMestra.Application.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> cadastrar(@Valid @RequestBody ProdutoRequest request) {
        ProdutoResponse produtoSalvo = produtoService.cadastrar(request);
        return ResponseEntity.ok(produtoSalvo);
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listar(
            @RequestParam(required = false) String nome) {
        List<ProdutoResponse> produtos = produtoService.listar();
        if (nome == null) {
            return ResponseEntity.ok(produtos);
        }
        String nomeNormalizado = nome.trim().toLowerCase(Locale.ROOT);
        return ResponseEntity.ok(produtos.stream()
                .filter(produto -> produto.nome() != null
                        && produto.nome().toLowerCase(Locale.ROOT).contains(nomeNormalizado))
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Integer id,
                                                     @Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
