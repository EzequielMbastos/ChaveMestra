package com.ChaveMestra.Application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IaRequest(
        @NotBlank(message = "pergunta é obrigatória")
        @Size(max = 1000, message = "pergunta deve ter no máximo 1000 caracteres")
        String pergunta,
        String tipo,
        Boolean confirmado,
        Integer interacaoIdConfirmacao
) {
    public IaRequest {
        confirmado = Boolean.TRUE.equals(confirmado);
    }
}
