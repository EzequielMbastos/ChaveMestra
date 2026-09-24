package com.ChaveMestra.Application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record AtendimentoItemCriacaoRequest(

        @NotNull(message = "id do item é obrigatório")
        Integer id,

        @NotBlank(message = "tipo é obrigatório")
        String tipo,

        @NotNull(message = "quantidade é obrigatória")
        @Positive(message = "quantidade deve ser maior que zero")
        Integer quantidade,

        @JsonAlias("preco_unitario")
        java.math.BigDecimal precoUnitario,

        @PositiveOrZero(message = "desconto não pode ser negativo")
        java.math.BigDecimal desconto,

        String observacao
) {
}
