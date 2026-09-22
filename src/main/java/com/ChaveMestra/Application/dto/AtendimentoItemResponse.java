package com.ChaveMestra.Application.dto;

import java.math.BigDecimal;

public record AtendimentoItemResponse(
        Integer id,
        Integer atendimentoId,
        Integer produtoId,
        String produtoNome,
        Integer servicoId,
        String servicoNome,
        Integer quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorTotal,
        BigDecimal desconto,
        String tipo,
        String observacao
) {}