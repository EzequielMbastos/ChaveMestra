package com.ChaveMestra.Application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MovimentoFinanceiroResponse(
        Integer id,
        Integer categoriaFinanceiraId,
        String categoriaFinanceiraNome,
        String tipo,
        Integer atendimentoId,
        String nome,
        String descricao,
        BigDecimal valor,
        LocalDateTime dataMovimento,
        LocalDate vencimento,
        String status
) {}