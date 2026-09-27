package com.ChaveMestra.Application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ChaveMestra.Application.dto.IaRequest;
import com.ChaveMestra.Application.dto.IaResponse;
import com.ChaveMestra.Application.exception.BusinessException;
import com.ChaveMestra.Application.model.IaInteracao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class IaService {

    private static final String SYSTEM_PROMPT = "Você é um assistente de negócio do sistema ChaveMestra. "
            + "Você tem acesso a tools para consultar dados em tempo real. Use-as sempre "
            + "que necessário para responder com dados concretos. Responda em português, claro e objetivo.";
    private static final int MAX_TOOL_ITERATIONS = 5;

    private final IaInteracaoService iaInteracaoService;
    private final ObjectMapper objectMapper;
    private final RestClient openRouterClient;
    private final RestClient internalClient;
    private final String apiKey;
    private final String model;
    private final String internalUser;
    private final String internalPassword;
    private final List<Map<String, Object>> tools;

    public IaService(
            IaInteracaoService iaInteracaoService,
            ObjectMapper objectMapper,
            @Value("${openrouter.api.url}") String apiUrl,
            @Value("${openrouter.api.key:}") String apiKey,
            @Value("${openrouter.api.model:deepseek/deepseek-chat}") String model,
            @Value("${openrouter.api.timeout-seconds:60}") int timeoutSeconds,
            @Value("${app.security.basic.user:admin}") String internalUser,
            @Value("${app.security.basic.password:admin123}") String internalPassword) {
        this.iaInteracaoService = iaInteracaoService;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.internalUser = internalUser;
        this.internalPassword = internalPassword;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(java.time.Duration.ofSeconds(timeoutSeconds));
        requestFactory.setReadTimeout(java.time.Duration.ofSeconds(timeoutSeconds));

        this.openRouterClient = RestClient.builder()
                .baseUrl(apiUrl)
                .requestFactory(requestFactory)
                .build();
        this.internalClient = RestClient.builder()
                .baseUrl("http://localhost:8080")
                .requestFactory(requestFactory)
                .build();
        this.tools = criarTools();
    }

    @Transactional
    public IaResponse chat(IaRequest request) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException("OPENROUTER_API_KEY não configurada");
        }

        long inicio = System.currentTimeMillis();
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        messages.add(Map.of("role", "user", "content", request.pergunta()));

        String resposta = null;
        for (int iteracao = 0; iteracao < MAX_TOOL_ITERATIONS; iteracao++) {
            JsonNode mensagem = chamarOpenRouter(messages);
            JsonNode toolCalls = mensagem.path("tool_calls");

            if (!toolCalls.isArray() || toolCalls.isEmpty()) {
                JsonNode content = mensagem.get("content");
                if (content == null || content.isNull() || content.asText().isBlank()) {
                    throw new BusinessException("OpenRouter retornou uma resposta vazia");
                }
                resposta = content.asText();
                break;
            }

            if (iteracao == MAX_TOOL_ITERATIONS - 1) {
                throw new BusinessException("OpenRouter excedeu o limite de chamadas de tools");
            }

            messages.add(objectMapper.convertValue(
                    mensagem,
                    new TypeReference<Map<String, Object>>() {}));
            for (JsonNode toolCall : toolCalls) {
                String toolCallId = toolCall.path("id").asText();
                String toolName = toolCall.path("function").path("name").asText();
                String arguments = toolCall.path("function").path("arguments").asText("{}");
                String toolResult = executarTool(toolName, arguments);
                messages.add(Map.of(
                        "role", "tool",
                        "tool_call_id", toolCallId,
                        "content", toolResult));
            }
        }

        if (resposta == null) {
            throw new BusinessException("OpenRouter não retornou uma resposta final");
        }

        BigDecimal tempoMs = BigDecimal.valueOf(System.currentTimeMillis() - inicio);
        IaInteracao interacao = iaInteracaoService.registrar(
                request.pergunta(),
                resposta,
                determinarTipo(request.tipo(), request.pergunta()),
                tempoMs,
                model);

        return new IaResponse(
                interacao.getId(),
                request.pergunta(),
                resposta,
                model,
                tempoMs,
                interacao.getDataInteracao());
    }

    private JsonNode chamarOpenRouter(List<Map<String, Object>> messages) {
        Map<String, Object> payload = Map.of(
                "model", model,
                "messages", messages,
                "tools", tools,
                "tool_choice", "auto");
        try {
            JsonNode response = openRouterClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(payload)
                    .retrieve()
                    .body(JsonNode.class);
            JsonNode mensagem = response == null ? null : response.path("choices").path(0).path("message");
            if (mensagem == null || mensagem.isMissingNode() || mensagem.isNull()) {
                throw new BusinessException("OpenRouter retornou uma resposta inválida");
            }
            return mensagem;
        } catch (RestClientResponseException exception) {
            throw new BusinessException("Falha na chamada ao OpenRouter: "
                    + exception.getStatusCode().value() + " " + exception.getResponseBodyAsString());
        } catch (RestClientException exception) {
            throw new BusinessException("Falha na chamada ao OpenRouter: " + exception.getMessage());
        }
    }

    private String executarTool(String nome, String argumentos) {
        try {
            JsonNode args = objectMapper.readTree(argumentos);
            String uri = switch (nome) {
                case "produtos_baixo_estoque" -> uriComLimite(
                        "/relatorios/produtos-baixo-estoque", args.path("limite").asInt(10));
                case "clientes_por_estado" -> "/relatorios/clientes-por-estado";
                case "financeiro_resumo" -> "/relatorios/financeiro-resumo";
                case "atendimentos_recentes" -> uriComLimite(
                        "/relatorios/atendimentos-recentes", args.path("limite").asInt(10));
                case "estoque_critico" -> "/relatorios/estoque-critico";
                case "relatorio_periodo" -> uriPeriodo(args);
                default -> throw new BusinessException("Tool não permitida: " + nome);
            };

            String result = internalClient.get()
                    .uri(uri)
                    .headers(headers -> headers.setBasicAuth(internalUser, internalPassword))
                    .retrieve()
                    .body(String.class);
            return result == null ? "null" : result;
        } catch (JsonProcessingException exception) {
            throw new BusinessException("Argumentos inválidos para tool " + nome);
        } catch (RestClientResponseException exception) {
            throw new BusinessException("Falha ao executar tool " + nome + ": "
                    + exception.getStatusCode().value() + " " + exception.getResponseBodyAsString());
        } catch (RestClientException exception) {
            throw new BusinessException("Falha ao executar tool " + nome + ": " + exception.getMessage());
        }
    }

    private String uriComLimite(String path, int limite) {
        return UriComponentsBuilder.fromPath(path)
                .queryParam("limite", limite)
                .build()
                .toUriString();
    }

    private String uriPeriodo(JsonNode args) {
        String inicio = args.path("inicio").asText("");
        String fim = args.path("fim").asText("");
        try {
            if (!inicio.isBlank()) {
                LocalDate.parse(inicio);
            }
            if (!fim.isBlank()) {
                LocalDate.parse(fim);
            }
        } catch (java.time.DateTimeException exception) {
            throw new BusinessException("Data inválida para tool relatorio_periodo; use YYYY-MM-DD");
        }
        return "/relatorios"
                + (inicio.isBlank() && fim.isBlank() ? "" : "?")
                + (inicio.isBlank() ? "" : "inicio=" + inicio)
                + (inicio.isBlank() || fim.isBlank() ? "" : "&")
                + (fim.isBlank() ? "" : "fim=" + fim);
    }

    private String determinarTipo(String tipoSolicitado, String pergunta) {
        if (tipoSolicitado != null) {
            String tipoNormalizado = tipoSolicitado.trim().toLowerCase(Locale.ROOT);
            if (List.of("relatorio", "consulta", "outro").contains(tipoNormalizado)) {
                return tipoNormalizado;
            }
        }
        String texto = pergunta.toLowerCase(Locale.ROOT);
        if (texto.contains("relatório") || texto.contains("relatorio")
                || texto.contains("resumo") || texto.contains("análise") || texto.contains("analise")) {
            return "relatorio";
        }
        if (texto.contains("?") || texto.startsWith("quantos") || texto.startsWith("qual")
                || texto.startsWith("quais") || texto.startsWith("quanto")) {
            return "consulta";
        }
        return "outro";
    }

    private List<Map<String, Object>> criarTools() {
        List<Map<String, Object>> definicoes = new ArrayList<>();
        definicoes.add(tool("produtos_baixo_estoque",
                "Lista produtos ativos com estoque abaixo de um limite (padrão 10).",
                Map.of("limite", propriedade("integer", "Limite de quantidade, padrão 10."))));
        definicoes.add(tool("clientes_por_estado",
                "Retorna a quantidade de clientes agrupada por estado.",
                Map.of()));
        definicoes.add(tool("financeiro_resumo",
                "Retorna entradas, saídas e saldo financeiro do mês atual.",
                Map.of()));
        definicoes.add(tool("atendimentos_recentes",
                "Lista os atendimentos mais recentes, até o limite informado (padrão 10).",
                Map.of("limite", propriedade("integer", "Número de atendimentos, padrão 10."))));
        definicoes.add(tool("estoque_critico",
                "Lista produtos ativos cuja quantidade em estoque está no mínimo ou abaixo dele.",
                Map.of()));
        definicoes.add(tool("relatorio_periodo",
                "Retorna resumo financeiro e movimentos de um período opcional.",
                Map.of(
                        "inicio", propriedade("string", "Data inicial no formato YYYY-MM-DD."),
                        "fim", propriedade("string", "Data final no formato YYYY-MM-DD."))));
        return List.copyOf(definicoes);
    }

    private Map<String, Object> tool(String nome, String descricao, Map<String, Object> propriedades) {
        Map<String, Object> parameters = Map.of(
                "type", "object",
                "properties", propriedades);
        Map<String, Object> function = Map.of(
                "name", nome,
                "description", descricao,
                "parameters", parameters);
        return Map.of("type", "function", "function", function);
    }

    private Map<String, Object> propriedade(String tipo, String descricao) {
        return Map.of("type", tipo, "description", descricao);
    }
}
