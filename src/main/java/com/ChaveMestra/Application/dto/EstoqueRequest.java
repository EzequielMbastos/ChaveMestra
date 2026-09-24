package com.ChaveMestra.Application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record EstoqueRequest(

        @NotNull(message = "produtoId é obrigatório")
        Integer produtoId,

        @NotNull(message = "quantidade é obrigatória")
        @PositiveOrZero(message = "quantidade não pode ser negativa")
        Integer quantidade,

        @NotNull(message = "minimo é obrigatório")
        @PositiveOrZero(message = "minimo não pode ser negativo")
        Integer minimo
) {}
