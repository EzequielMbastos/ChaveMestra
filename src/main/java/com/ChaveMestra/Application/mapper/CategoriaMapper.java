package com.ChaveMestra.Application.mapper;

import com.ChaveMestra.Application.dto.CategoriaRequest;
import com.ChaveMestra.Application.dto.CategoriaResponse;
import com.ChaveMestra.Application.model.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public CategoriaResponse toResponse(Categoria categoria) {
            return
                new CategoriaResponse(
                        categoria.getId(),
                        categoria.getNome(),
                        categoria.getDescricao());
    }

    public Categoria toCategoria(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        return categoria;
    }
}