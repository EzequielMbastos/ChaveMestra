package com.ChaveMestra.Application.mapper;

import com.ChaveMestra.Application.dto.EstoqueRequest;
import com.ChaveMestra.Application.dto.EstoqueResponse;
import com.ChaveMestra.Application.model.Estoque;
import com.ChaveMestra.Application.model.Produto;
import org.springframework.stereotype.Component;

@Component
public class EstoqueMapper {


    public EstoqueResponse toResponse(Estoque estoque) {
        return new EstoqueResponse(
                estoque.getId(),
                estoque.getProduto() != null ? estoque.getProduto().getId() : null,
                estoque.getProduto() != null ? estoque.getProduto().getNome() : null,
                estoque.getQuantidade(),
                estoque.getMinimo(),
                estoque.getQuantidade() <= estoque.getMinimo(),  // ← calculado
                estoque.getDtEntrada(),
                estoque.getDtAtualizacao()
        );
    }
        public Estoque toEntity (EstoqueRequest request, Produto produto){
            Estoque estoque = new Estoque();
            estoque.setProduto(produto);
            estoque.setQuantidade(request.quantidade());
            estoque.setMinimo(request.minimo());
            // dtEntrada e dtAtualizacao NÃO são setados → @PrePersist preenche
            return estoque;
        }
}

