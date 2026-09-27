package com.ChaveMestra.Application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

public record AtendimentoRequest(

        @JsonAlias("cliente_id")
        Integer clienteId,  // opcional (SQL permite null)

        @NotBlank(message = "forma de pagamento é obrigatória")
        @JsonAlias("forma_pagamento")
        String formaPagamento,

        @PositiveOrZero(message = "desconto não pode ser negativo")
        BigDecimal desconto,

        String observacao,

        @jakarta.validation.Valid
        List<AtendimentoItemCriacaoRequest> itens
) {}
