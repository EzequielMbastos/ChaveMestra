package com.ChaveMestra.Application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoriaFinanceiraRequest(
        @NotBlank(message = "nome é obrigatório")
        @Size(max = 50, message = "nome não pode exceder 50 caracteres")
        String nome,

        @NotBlank(message = "tipo é obrigatório")
        @Pattern(regexp = "entrada|saida", message = "tipo deve ser 'entrada' ou 'saida'")
        String tipo,

        @Size(max = 100, message = "descrição não pode exceder 100 caracteres")
        String descricao
) {
}
