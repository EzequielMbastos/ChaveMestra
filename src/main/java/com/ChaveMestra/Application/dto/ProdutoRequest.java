package com.ChaveMestra.Application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoRequest(
        @NotBlank(message = "código do catálogo é obrigatório")
        String codigoCatalogo,

        @NotBlank(message = "nome é obrigatório")
        String nome,

        @NotNull(message = "preço de venda é obrigatório")
        @DecimalMin(value = "0.01", message = "preço de venda deve ser maior que zero")
        BigDecimal precoVenda,

        @NotNull(message = "preço de custo é obrigatório")
        @DecimalMin(value = "0.00", message = "preço de custo não pode ser negativo")
        BigDecimal precoCusto,

        @NotNull(message = "categoriaId é obrigatório")
        Integer categoriaId,

        Integer fornecedorId
) {
}
