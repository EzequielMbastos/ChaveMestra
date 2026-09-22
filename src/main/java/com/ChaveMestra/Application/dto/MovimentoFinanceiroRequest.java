package com.ChaveMestra.Application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MovimentoFinanceiroRequest(

        @NotNull(message = "categoriaFinanceiraId é obrigatório")
        Integer categoriaFinanceiraId,

        Integer atendimentoId,  // opcional

        String nome,

        String descricao,

        @NotNull(message = "valor é obrigatório")
        @Positive(message = "valor deve ser maior que zero")
        BigDecimal valor,

        LocalDateTime dataMovimento,  // se null, o service preenche

        LocalDate vencimento,

        String status  // se null, o service assume "pendente"
) {}