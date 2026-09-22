package com.ChaveMestra.Application.controller;

import com.ChaveMestra.Application.dto.ServicoRequest;
import com.ChaveMestra.Application.dto.ServicoResponse;
import com.ChaveMestra.Application.service.ServicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoController {
    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService){
        this.servicoService = servicoService;
    }

    @PostMapping
    public ResponseEntity<ServicoResponse> cadastrar(@RequestBody ServicoRequest request){
        ServicoResponse servicoSalvo = servicoService.cadastrar(request);
        return ResponseEntity.ok(servicoSalvo);
    }

    @GetMapping
    public  ResponseEntity<List<ServicoResponse>> listar(){
        return ResponseEntity.ok(servicoService.listar());
    }

    @GetMapping("/{id}")
    public  ResponseEntity<ServicoResponse> buscarPorId(@PathVariable Integer id){
        ServicoResponse servico = servicoService.buscarPorId(id);
        return ResponseEntity.ok(servico);
    }

    @DeleteMapping("/{id}")
    public   ResponseEntity<Void> deletar(@PathVariable Integer id){
        servicoService.excluir(id);
        return  ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public  ResponseEntity<ServicoResponse> atualizar(@PathVariable Integer id, @RequestBody ServicoRequest request){
        ServicoResponse servicoAtualizado = servicoService.atualizar(id, request);
        return ResponseEntity.ok(servicoAtualizado);
    }
}
