package com.ChaveMestra.Apllicattion.controller;
import com.ChaveMestra.Apllicattion.service.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ChaveMestra.Apllicattion.dto.CategoriaRequest;
import com.ChaveMestra.Apllicattion.dto.CategoriaResponse;


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
}