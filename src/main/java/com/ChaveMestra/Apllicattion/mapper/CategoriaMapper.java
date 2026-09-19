package com.ChaveMestra.Apllicattion.mapper;

import com.ChaveMestra.Apllicattion.dto.CategoriaRequest;
import com.ChaveMestra.Apllicattion.dto.CategoriaResponse;
import com.ChaveMestra.Apllicattion.model.Categoria;
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