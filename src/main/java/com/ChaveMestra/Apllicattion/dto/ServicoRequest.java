package com.ChaveMestra.Apllicattion.dto;

import java.math.BigDecimal;

public record ServicoRequest(String nome, String descricao, BigDecimal precoBase) {
}
