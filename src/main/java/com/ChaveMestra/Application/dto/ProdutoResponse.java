package com.ChaveMestra.Application.dto;

import java.math.BigDecimal;

public record ProdutoResponse(
        Integer id,
        String codigoCatalogo,
        String nome,
        BigDecimal precoVenda,
        BigDecimal precoCusto,
        Integer categoriaId,
        String categoriaNome,
        Integer fornecedorId,
        String fornecedorNome,
        Boolean ativo
) {
}
