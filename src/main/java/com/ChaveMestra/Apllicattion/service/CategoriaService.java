package com.ChaveMestra.Apllicattion.service;
import com.ChaveMestra.Apllicattion.model.Categoria;
import org.springframework.stereotype.Service;
import com.ChaveMestra.Apllicattion.repository.CategoriaRepository;

import java.util.List;
@Service
public class CategoriaService {

    private CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository)
    {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria cadastrar(Categoria categoria)
    {
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listar(){
        return categoriaRepository.findAll();
    }

}
