package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.exception.IaRateLimitException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class IaRateLimitService {

    private final Map<String, List<Instant>> requisicoesPorUsuario = new ConcurrentHashMap<>();
    private final int requestsPerHour;
    private final int requestsPerDay;

    public IaRateLimitService(
            @Value("${ia.rate-limit.requests-per-hour:20}") int requestsPerHour,
            @Value("${ia.rate-limit.requests-per-day:100}") int requestsPerDay) {
        this.requestsPerHour = requestsPerHour;
        this.requestsPerDay = requestsPerDay;
    }

    public void verificarLimite(String username) {
        Instant agora = Instant.now();
        Instant umaHoraAtras = agora.minus(1, ChronoUnit.HOURS);
        Instant umDiaAtras = agora.minus(1, ChronoUnit.DAYS);

        List<Instant> requisicoes = requisicoesPorUsuario.computeIfAbsent(
                username, k -> new java.util.concurrent.CopyOnWriteArrayList<>()
        );

        // Limpar requisições antigas
        List<Instant> requisicoesRecentes = requisicoes.stream()
                .filter(t -> t.isAfter(umDiaAtras))
                .collect(Collectors.toList());

        requisicoesPorUsuario.put(username, requisicoesRecentes);

        // Verificar limite por hora
        long countHora = requisicoesRecentes.stream()
                .filter(t -> t.isAfter(umaHoraAtras))
                .count();

        if (countHora >= requestsPerHour) {
            Instant primeiraRequisicaoHora = requisicoesRecentes.stream()
                    .filter(t -> t.isAfter(umaHoraAtras))
                    .min(Instant::compareTo)
                    .orElse(agora);
            long segundosParaRetry = ChronoUnit.SECONDS.between(agora, primeiraRequisicaoHora.plus(1, ChronoUnit.HOURS));
            throw new IaRateLimitException(
                    String.format("Você atingiu o limite de %d requisições por hora. Tente novamente em %d minutos.",
                            requestsPerHour, (segundosParaRetry / 60) + 1),
                    segundosParaRetry
            );
        }

        // Verificar limite por dia
        if (requisicoesRecentes.size() >= requestsPerDay) {
            Instant primeiraRequisicaoDia = requisicoesRecentes.stream()
                    .min(Instant::compareTo)
                    .orElse(agora);
            long segundosParaRetry = ChronoUnit.SECONDS.between(agora, primeiraRequisicaoDia.plus(1, ChronoUnit.DAYS));
            throw new IaRateLimitException(
                    String.format("Você atingiu o limite de %d requisições por dia. Tente novamente em %d minutos.",
                            requestsPerDay, (segundosParaRetry / 60) + 1),
                    segundosParaRetry
            );
        }

        // Adicionar nova requisição
        requisicoesRecentes.add(agora);
        requisicoesPorUsuario.put(username, requisicoesRecentes);
    }
}
