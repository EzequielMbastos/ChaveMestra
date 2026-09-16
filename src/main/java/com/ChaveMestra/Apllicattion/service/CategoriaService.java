package com.ChaveMestra.Apllicattion.service;
import com.ChaveMestra.Apllicattion.dto.CategoriaRequest;
import com.ChaveMestra.Apllicattion.dto.CategoriaResponse;
import com.ChaveMestra.Apllicattion.mapper.CategoriaMapper;
import com.ChaveMestra.Apllicattion.model.Categoria;
import org.springframework.stereotype.Service;
import com.ChaveMestra.Apllicattion.repository.CategoriaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    public CategoriaService(CategoriaRepository categoriaRepository,
                            CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public CategoriaResponse cadastrar(CategoriaRequest request)
    {
        Categoria categoria = categoriaMapper.toCategoria(request);
        Categoria categoriaSalva = categoriaRepository.save(categoria);
        return categoriaMapper.toResponse(categoriaSalva);
    }

    public List<CategoriaResponse> listar(){
        List<CategoriaResponse> listCategoriaResponse =  categoriaRepository.findAll().stream()
                .map(categoria -> categoriaMapper.toResponse(categoria))
                .collect(Collectors.toList());
        return listCategoriaResponse;
    }
}
