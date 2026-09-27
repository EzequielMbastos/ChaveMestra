package com.ChaveMestra.Application.controller;
import com.ChaveMestra.Application.dto.ClienteRequest;
import com.ChaveMestra.Application.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ChaveMestra.Application.dto.ClienteResponse;


import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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


    @GetMapping(params = "!nome")
    public ResponseEntity<List<ClienteResponse>> listar(){
        return ResponseEntity.ok(clienteService.listar());
    }

    @GetMapping(value = {"", "/buscar"}, params = "nome")
    public ResponseEntity<List<ClienteResponse>> buscar(@RequestParam String nome) {
        String nomeNormalizado = nome.trim().toLowerCase(Locale.ROOT);
        List<ClienteResponse> clientes = clienteService.listar().stream()
                .filter(cliente -> cliente.nome() != null
                        && cliente.nome().toLowerCase(Locale.ROOT).contains(nomeNormalizado))
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientes);
    }

    @GetMapping(value = "/{id}", params = "!nome")
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