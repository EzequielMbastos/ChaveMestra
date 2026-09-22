package com.ChaveMestra.Application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record AtendimentoItemRequest(

        @NotNull(message = "atendimentoId é obrigatório")
        Integer atendimentoId,

        Integer produtoId,    // obrigatório se tipo = "produto"

        Integer servicoId,    // obrigatório se tipo = "servico"

        @NotNull(message = "quantidade é obrigatória")
        @Positive(message = "quantidade deve ser maior que zero")
        Integer quantidade,

        @NotNull(message = "valorUnitario é obrigatório")
        @Positive(message = "valorUnitario deve ser maior que zero")
        BigDecimal valorUnitario,

        @PositiveOrZero(message = "desconto não pode ser negativo")
        BigDecimal desconto,

        @NotBlank(message = "tipo é obrigatório")
        String tipo,          // "produto" ou "servico"

        String observacao
) {}