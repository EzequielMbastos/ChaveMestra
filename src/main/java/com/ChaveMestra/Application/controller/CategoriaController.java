package com.ChaveMestra.Application.controller;
import com.ChaveMestra.Application.service.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ChaveMestra.Application.dto.CategoriaRequest;
import com.ChaveMestra.Application.dto.CategoriaResponse;


import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService){
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> cadastrar(@RequestBody CategoriaRequest request ){
        CategoriaResponse categoriaSalva = categoriaService.cadastrar(request);
        return ResponseEntity.ok(categoriaSalva);
    }


    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar(){
        return ResponseEntity.ok(categoriaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Integer id) {
        CategoriaResponse categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        categoriaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Integer id,
                                                       @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.atualizar(id, request));
    }
}