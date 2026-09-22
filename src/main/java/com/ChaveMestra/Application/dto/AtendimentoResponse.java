package com.ChaveMestra.Application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AtendimentoResponse(
        Integer id,
        Integer clienteId,
        String clienteNome,
        LocalDateTime data,
        String formaPagamento,
        BigDecimal valorTotal,
        BigDecimal desconto,
        BigDecimal valorLiquido,
        String status,
        String observacao
) {}