package com.ChaveMestra.Application.dto;
import java.time.LocalDateTime;

public record EstoqueResponse(
        Integer id,
        Integer produtoId,
        String produtoNome,
        Integer quantidade,
        Integer minimo,
        Boolean estoqueBaixo,
        LocalDateTime dtEntrada,
        LocalDateTime dtAtualizacao
) {
}
