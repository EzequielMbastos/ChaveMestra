package com.ChaveMestra.Apllicattion.controller;

import com.ChaveMestra.Apllicattion.dto.CategoriaFinanceiraRequest;
import com.ChaveMestra.Apllicattion.dto.CategoriaFinanceiraResponse;
import com.ChaveMestra.Apllicattion.model.CategoriaFinanceira;
import com.ChaveMestra.Apllicattion.service.CategoriaFinanceiraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categoria-financeira")
public class CategoriaFinanceiraController {

    private final CategoriaFinanceiraService categoriaFinanceiraService;

    public CategoriaFinanceiraController(CategoriaFinanceiraService categoriaFinanceiraService) {
        this.categoriaFinanceiraService = categoriaFinanceiraService;
    }

    @PostMapping
    public ResponseEntity<CategoriaFinanceiraResponse> cadastrar(@RequestBody CategoriaFinanceiraRequest request ){
        CategoriaFinanceiraResponse categoriaFinanceiraSalva = categoriaFinanceiraService.cadastrar(request);
        return ResponseEntity.ok(categoriaFinanceiraSalva);
    }


    @GetMapping
    public ResponseEntity<List<CategoriaFinanceiraResponse>> listar(){
        return ResponseEntity.ok(categoriaFinanceiraService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaFinanceiraResponse> buscarPorId(@PathVariable Integer id) {
        CategoriaFinanceiraResponse categoriaFinanceira = categoriaFinanceiraService.buscarPorId(id);
        return ResponseEntity.ok(categoriaFinanceira);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        categoriaFinanceiraService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaFinanceiraResponse> atualizar(@PathVariable Integer id, @RequestBody CategoriaFinanceiraRequest request) {
        CategoriaFinanceiraResponse categoriaFinanceiraAtualizada = categoriaFinanceiraService.atualizar(id, request);
        return ResponseEntity.ok(categoriaFinanceiraAtualizada);
    }
}


