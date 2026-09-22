package com.ChaveMestra.Application.dto;

import java.math.BigDecimal;

public record ServicoRequest(String nome, String descricao, BigDecimal precoBase) {
}
