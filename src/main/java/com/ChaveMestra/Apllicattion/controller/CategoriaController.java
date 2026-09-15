package com.ChaveMestra.Apllicattion.controller;
import com.ChaveMestra.Apllicattion.model.Categoria;
import com.ChaveMestra.Apllicattion.service.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService){
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<Categoria> cadastrar(@RequestBody Categoria categoria ){
        Categoria categoriaSalva = categoriaService.cadastrar(categoria);
        return ResponseEntity.ok(categoriaSalva);
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listar(){
        return ResponseEntity.ok(categoriaService.listar());
    }

}