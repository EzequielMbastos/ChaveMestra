package com.ChaveMestra.Application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record AtendimentoRequest(

        Integer clienteId,  // opcional (SQL permite null)

        @NotBlank(message = "forma de pagamento é obrigatória")
        String formaPagamento,

        @NotNull(message = "desconto é obrigatório")
        @PositiveOrZero(message = "desconto não pode ser negativo")
        BigDecimal desconto,

        String observacao
) {}