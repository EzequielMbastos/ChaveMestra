package com.ChaveMestra.Application.controller;
import com.ChaveMestra.Application.dto.FornecedorRequest;
import com.ChaveMestra.Application.service.FornecedorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ChaveMestra.Application.dto.FornecedorResponse;


import java.util.List;

@RestController
@RequestMapping("/fornecedores")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService){
        this.fornecedorService = fornecedorService;
    }

    @PostMapping
    public ResponseEntity<FornecedorResponse> cadastrar(@RequestBody FornecedorRequest request ){
        FornecedorResponse clienteSalvo = fornecedorService.cadastrar(request);
        return ResponseEntity.ok(clienteSalvo);
    }


    @GetMapping
    public ResponseEntity<List<FornecedorResponse>> listar(){
        return ResponseEntity.ok(fornecedorService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponse> buscarPorId(@PathVariable Integer id) {
        FornecedorResponse fornecedor = fornecedorService.buscarPorId(id);
        return ResponseEntity.ok(fornecedor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        fornecedorService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponse> atualizar(@PathVariable Integer id, @RequestBody FornecedorRequest request) {
        FornecedorResponse fornecedorAtualizado = fornecedorService.atualizar(id, request);
        return ResponseEntity.ok(fornecedorAtualizado);
    }
}