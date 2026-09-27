package com.ChaveMestra.Application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IaResponse(
        Integer interacaoId,
        String pergunta,
        String resposta,
        String modeloUsado,
        BigDecimal tempoRespostaMs,
        LocalDateTime dataInteracao
) {}
