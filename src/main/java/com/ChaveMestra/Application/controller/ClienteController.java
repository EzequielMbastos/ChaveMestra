package com.ChaveMestra.Application.controller;
import com.ChaveMestra.Application.dto.ClienteRequest;
import com.ChaveMestra.Application.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ChaveMestra.Application.dto.ClienteResponse;


import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService){
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> cadastrar(@RequestBody ClienteRequest request ){
        ClienteResponse clienteSalvo = clienteService.cadastrar(request);
        return ResponseEntity.ok(clienteSalvo);
    }


    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar(){
        return ResponseEntity.ok(clienteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Integer id) {
        ClienteResponse cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        clienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar(@PathVariable Integer id, @RequestBody ClienteRequest request) {
        ClienteResponse clienteAtualizado = clienteService.atualizar(id, request);
        return ResponseEntity.ok(clienteAtualizado);
    }
}