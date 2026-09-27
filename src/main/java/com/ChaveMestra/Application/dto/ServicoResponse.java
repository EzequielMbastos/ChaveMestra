package com.ChaveMestra.Application.dto;

import java.math.BigDecimal;

public record ServicoResponse(Integer id, String nome, String descricao, BigDecimal precoBase) {
}
