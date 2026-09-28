package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class IaGuardrailService {

    private static final List<Pattern> PADROES_SUSPEITOS = List.of(
            Pattern.compile("(?i)ignore\\s+(as\\s+)?instru[çc][õo]es"),
            Pattern.compile("(?i)ignore\\s+(the\\s+)?(previous|above)\\s+instructions"),
            Pattern.compile("(?i)system\\s*:.*voc[êe]\\s+(agora\\s+)?[ée]"),
            Pattern.compile("(?i)mostre\\s+.{0,40}\\bprompt"),
            Pattern.compile("(?i)revele?\\s+(a\\s+)?(sua\\s+)?(senha|chave|api\\s*key)"),
            Pattern.compile("(?i)(senha|password)\\s+do\\s+(banco|database)"),
            Pattern.compile("(?i)jailbreak|DAN\\s+mode|developer\\s+mode"),
            Pattern.compile("(?i)finja\\s+que\\s+voc[êe]\\s+[ée]"),
            Pattern.compile("(?i)pretend\\s+you\\s+are"));

    private static final Set<String> PALAVRAS_SENSIVEIS = Set.of(
            "api_key",
            "apikey",
            "openrouter_api_key",
            "database_password",
            "spring_datasource_password");
    private static final Pattern TOKEN_API = Pattern.compile("sk-[a-zA-Z0-9-]{20,}");
    private static final String RESPOSTA_BLOQUEADA =
            "Resposta bloqueada por conter informação sensível.";

    public void validarPergunta(String pergunta) {
        if (pergunta == null || pergunta.isBlank()) {
            return;
        }

        for (Pattern padrao : PADROES_SUSPEITOS) {
            if (padrao.matcher(pergunta).find()) {
                throw new BusinessException(
                        "Sua pergunta contém padrões não permitidos. Por favor, reformule de forma direta.");
            }
        }

        String perguntaNormalizada = pergunta.toLowerCase(Locale.ROOT);
        for (String palavra : PALAVRAS_SENSIVEIS) {
            if (perguntaNormalizada.contains(palavra)) {
                throw new BusinessException(
                        "Não posso responder sobre informações de configuração interna.");
            }
        }
    }

    public void validarResposta(String resposta) {
        if (resposta == null) {
            return;
        }

        if (TOKEN_API.matcher(resposta).find()
                || resposta.toLowerCase(Locale.ROOT).contains("spring_datasource_password")) {
            throw new BusinessException(RESPOSTA_BLOQUEADA);
        }
    }
}
