package com.ChaveMestra.Application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "nome é obrigatório")
        @Size(max = 50, message = "nome não pode exceder 50 caracteres")
        String nome,

        @Size(max = 100, message = "descrição não pode exceder 100 caracteres")
        String descricao
) {}

