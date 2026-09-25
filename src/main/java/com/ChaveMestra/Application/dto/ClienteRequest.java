package com.ChaveMestra.Application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank(message = "nome é obrigatório")
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
        String cpf,

        String telefone,

        String endereco,

        @Size(max = 2, message = "estado deve ter no máximo 2 caracteres")
        String estado
) {
}
