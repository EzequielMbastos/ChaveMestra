package com.ChaveMestra.Application.mapper;

import com.ChaveMestra.Application.dto.ProdutoRequest;
import com.ChaveMestra.Application.dto.ProdutoResponse;
import com.ChaveMestra.Application.model.Produto;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    public ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getCodigoCatalogo(),
                produto.getNome(),
                produto.getPrecoVenda(),
                produto.getPrecoCusto(),
                produto.getCategoria() != null ? produto.getCategoria().getId() : null,
                produto.getCategoria() != null ? produto.getCategoria().getNome() : null,
                produto.getFornecedor() != null ? produto.getFornecedor().getId() : null,
                produto.getFornecedor() != null ? produto.getFornecedor().getNome() : null,
                produto.getAtivo()
        );
    }

    public Produto toEntity(ProdutoRequest request) {
        Produto produto = new Produto();
        produto.setCodigoCatalogo(request.codigoCatalogo());
        produto.setNome(request.nome());
        produto.setPrecoVenda(request.precoVenda());
        produto.setPrecoCusto(request.precoCusto());
        produto.setAtivo(true);
        return produto;
    }
}
