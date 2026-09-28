package com.ChaveMestra.Application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IaAvaliacaoRequest(
        @NotNull(message = "avaliação é obrigatória")
        @Min(value = 1, message = "avaliação deve ser entre 1 e 5")
        @Max(value = 5, message = "avaliação deve ser entre 1 e 5")
        Integer avaliacao,

        @Size(max = 500, message = "comentário deve ter no máximo 500 caracteres")
        String comentario
) {
}
