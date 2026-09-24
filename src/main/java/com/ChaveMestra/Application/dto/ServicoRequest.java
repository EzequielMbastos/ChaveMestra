package com.ChaveMestra.Application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ServicoRequest(
        @NotBlank(message = "nome é obrigatório")
        String nome,

        String descricao,

        @NotNull(message = "preço base é obrigatório")
        @DecimalMin(value = "0.01", message = "preço base deve ser maior que zero")
        BigDecimal precoBase
) {
}
