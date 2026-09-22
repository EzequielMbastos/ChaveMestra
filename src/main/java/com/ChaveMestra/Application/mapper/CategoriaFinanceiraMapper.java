package com.ChaveMestra.Application.mapper;

import com.ChaveMestra.Application.dto.CategoriaFinanceiraRequest;
import com.ChaveMestra.Application.dto.CategoriaFinanceiraResponse;
import com.ChaveMestra.Application.model.CategoriaFinanceira;
import org.springframework.stereotype.Component;

@Component
public class CategoriaFinanceiraMapper {

    public CategoriaFinanceiraResponse toResponse(CategoriaFinanceira categoriaFinanceira){
        return new CategoriaFinanceiraResponse(
                categoriaFinanceira.getId(),
                categoriaFinanceira.getNome(),
                categoriaFinanceira.getTipo(),
                categoriaFinanceira.getDescricao()
        );
    }

    public CategoriaFinanceira toCategoriaFinanceira(CategoriaFinanceiraRequest request){
        CategoriaFinanceira categoriaFinanceira = new CategoriaFinanceira();
        categoriaFinanceira.setNome(request.nome());
        categoriaFinanceira.setTipo(request.tipo());
        categoriaFinanceira.setDescricao(request.descricao());
        return categoriaFinanceira;
    }
}


