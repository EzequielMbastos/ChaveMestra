package com.ChaveMestra.Application.dto;

import java.math.BigDecimal;

public record ProdutoRequest(
        String codigoCatalogo,
        String nome,
        BigDecimal precoVenda,
        BigDecimal precoCusto,
        Integer categoriaId,
        Integer fornecedorId
) {
}
