package com.ChaveMestra.Application.dto;

import java.math.BigDecimal;

public record ServicoResponse(Integer Id, String nome, String descricao, BigDecimal precoBase) {
}
