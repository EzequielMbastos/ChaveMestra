package com.ChaveMestra.Application.service;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.ChaveMestra.Application.dto.IaRequest;
import com.ChaveMestra.Application.dto.IaResponse;
import com.ChaveMestra.Application.exception.BusinessException;
import com.ChaveMestra.Application.model.IaInteracao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class IaService {

    private static final String SYSTEM_PROMPT = "Você é um assistente de negócio do sistema ChaveMestra. "
            + "Você tem acesso a tools para consultar dados em tempo real. Use-as sempre "
            + "que necessário para responder com dados concretos. Responda em português, claro e objetivo. "
            + "Quando o usuário pedir para CRIAR um atendimento, você deve: "
            + "1. Primeiro, buscar o cliente pelo nome (use buscar_cliente_por_nome). "
            + "2. Buscar os produtos/serviços solicitados. "
            + "3. APRESENTAR um resumo do que será criado (cliente, itens, valores, forma de pagamento). "
            + "4. PERGUNTAR 'Confirma a criação?' e AGUARDAR a resposta. "
            + "5. Só criar o atendimento se o usuário responder 'sim' ou 'confirmo'. "
            + "Se o usuário não confirmar, NÃO execute a ação. ";
    private static final int MAX_TOOL_ITERATIONS = 5;
    private static final List<Long> RATE_LIMIT_RETRY_DELAYS_SECONDS = List.of(2L, 4L, 8L);

    private final IaInteracaoService iaInteracaoService;
    private final ObjectMapper objectMapper;
    private final RestClient openRouterClient;
    private final RestClient internalClient;
    private final String apiKey;
    private final String model;
    private final List<String> providerOrder;
    private final String internalUser;
    private final String internalPassword;
    private final List<Map<String, Object>> tools;

    public IaService(
            IaInteracaoService iaInteracaoService,
            ObjectMapper objectMapper,
            @Value("${openrouter.api.url}") String apiUrl,
            @Value("${openrouter.api.key:}") String apiKey,
            @Value("${openrouter.api.model:deepseek/deepseek-chat}") String model,
            @Value("${openrouter.api.provider-order:DeepInfra,Together,Fireworks}") String providerOrder,
            @Value("${openrouter.api.timeout-seconds:60}") int timeoutSeconds,
            @Value("${app.security.basic.user:admin}") String internalUser,
            @Value("${app.security.basic.password:admin123}") String internalPassword) {
        this.iaInteracaoService = iaInteracaoService;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.providerOrder = Arrays.stream(providerOrder.split(","))
                .map(String::trim)
                .filter(provider -> !provider.isEmpty())
                .toList();
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

    public IaResponse chat(IaRequest request) {
        long inicio = System.currentTimeMillis();
        if (Boolean.TRUE.equals(request.confirmado())) {
            return confirmarAtendimento(request, inicio);
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException("OPENROUTER_API_KEY não configurada");
        }

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "system",
                "content", SYSTEM_PROMPT + "A flag 'confirmado' indica se o usuário já confirmou uma ação pendente. "
                        + "Se 'confirmado' for true, você pode executar a ação proposta anteriormente. "
                        + "Se for false, apenas proponha. Neste pedido, confirmado=false."));
        messages.add(Map.of("role", "user", "content", request.pergunta()));

        String resposta = null;
        String[] acaoPendente = new String[1];
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
                String toolResult = executarTool(toolName, arguments, acaoPendente);
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
                model,
                acaoPendente[0]);

        if (acaoPendente[0] != null && interacao.getId() == null) {
            throw new BusinessException("Não foi possível persistir a ação pendente para confirmação");
        }
        if (acaoPendente[0] != null) {
            resposta += "\nPara confirmar, responda 'sim' ou 'confirmo' com confirmado=true e "
                    + "interacaoIdConfirmacao=" + interacao.getId() + ".";
            iaInteracaoService.atualizarResposta(interacao.getId(), resposta);
        }

        return new IaResponse(
                interacao.getId(),
                request.pergunta(),
                resposta,
                model,
                tempoMs,
                interacao.getDataInteracao());
    }

    private IaResponse confirmarAtendimento(IaRequest request, long inicio) {
        if (request.interacaoIdConfirmacao() == null) {
            throw new BusinessException("Informe interacaoIdConfirmacao para aprovar a ação pendente");
        }
        String respostaConfirmacao = request.pergunta().trim().toLowerCase(Locale.ROOT)
                .replaceAll("[.!?,]+$", "").trim();
        if (!Set.of("sim", "confirmo").contains(respostaConfirmacao)) {
            throw new BusinessException("Para executar a ação pendente, responda somente 'sim' ou 'confirmo'");
        }

        String acao = iaInteracaoService.confirmarAcao(
                request.interacaoIdConfirmacao(),
                this::executarAtendimento);
        BigDecimal tempoMs = BigDecimal.valueOf(System.currentTimeMillis() - inicio);
        String resposta = "Atendimento criado com sucesso. Identificador: "
                + acao.substring(acao.indexOf('#') + 1) + ".";
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
        Map<String, Object> providerPrefs = Map.of(
                "order", providerOrder,
                "allow_fallbacks", true);
        Map<String, Object> payload = Map.of(
                "model", model,
                "messages", messages,
                "tools", tools,
                "tool_choice", "auto",
                "provider", providerPrefs);
        for (int tentativa = 0; ; tentativa++) {
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
                if (exception.getStatusCode().value() == 429
                        && tentativa < RATE_LIMIT_RETRY_DELAYS_SECONDS.size()) {
                    try {
                        Thread.sleep(RATE_LIMIT_RETRY_DELAYS_SECONDS.get(tentativa) * 1_000);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        throw new BusinessException("Retry da chamada ao OpenRouter foi interrompido");
                    }
                    continue;
                }
                throw new BusinessException("Falha na chamada ao OpenRouter: "
                        + exception.getStatusCode().value() + " " + exception.getResponseBodyAsString());
            } catch (RestClientException exception) {
                throw new BusinessException("Falha na chamada ao OpenRouter: " + exception.getMessage());
            }
        }
    }

    private String executarTool(String nome, String argumentos, String[] acaoPendente) {
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
                case "buscar_cliente_por_nome" -> uriComNome("/clientes", args.path("nome").asText(""));
                case "buscar_produto_por_nome" -> uriComNome("/produtos", args.path("nome").asText(""));
                case "buscar_servico_por_nome" -> uriComNome("/servicos", args.path("nome").asText(""));
                case "listar_clientes_recentes" -> "/clientes";
                case "listar_formas_pagamento" -> null;
                case "criar_atendimento" -> null;
                default -> throw new BusinessException("Tool não permitida: " + nome);
            };

            if ("listar_formas_pagamento".equals(nome)) {
                return "[\"pix\",\"dinheiro\",\"cartao_credito\",\"cartao_debito\"]";
            }
            if ("criar_atendimento".equals(nome)) {
                String rascunho = validarRascunhoAtendimento(args);
                if (acaoPendente[0] != null && !acaoPendente[0].equals(rascunho)) {
                    throw new BusinessException("A IA propôs mais de um atendimento na mesma interação");
                }
                acaoPendente[0] = rascunho;
                return "Ação pendente de confirmação; o atendimento NÃO foi criado. "
                        + "Apresente ao usuário o resumo com cliente, itens, valores e forma de pagamento, "
                        + "e pergunte 'Confirma a criação?'.";
            }
            String result = internalClient.get()
                    .uri(uri)
                    .headers(headers -> headers.setBasicAuth(internalUser, internalPassword))
                    .retrieve()
                    .body(String.class);
            return result == null ? "null" : result;
        } catch (JacksonException exception) {
            throw new BusinessException("Argumentos inválidos para tool " + nome);
        } catch (RestClientResponseException exception) {
            throw new BusinessException("Falha ao executar tool " + nome + ": "
                    + exception.getStatusCode().value() + " " + exception.getResponseBodyAsString());
        } catch (RestClientException exception) {
            throw new BusinessException("Falha ao executar tool " + nome + ": " + exception.getMessage());
        }
    }

    private String validarRascunhoAtendimento(JsonNode args) {
        JsonNode clienteId = args.path("clienteId");
        JsonNode formaPagamento = args.path("formaPagamento");
        JsonNode itens = args.path("itens");
        if (!clienteId.isIntegralNumber() || clienteId.intValue() <= 0
                || !formaPagamento.isTextual()
                || !Set.of("pix", "dinheiro", "cartao_credito", "cartao_debito")
                        .contains(formaPagamento.asText())
                || !itens.isArray() || itens.isEmpty()) {
            throw new BusinessException("Dados inválidos para criar atendimento");
        }
        List<Map<String, Object>> itensValidados = new ArrayList<>();
        for (JsonNode item : itens) {
            String tipo = item.path("tipo").asText("");
            if (!Set.of("produto", "servico").contains(tipo)
                    || !item.path("id").isIntegralNumber() || item.path("id").intValue() <= 0
                    || !item.path("quantidade").isIntegralNumber() || item.path("quantidade").intValue() <= 0) {
                throw new BusinessException("Item inválido no rascunho de atendimento");
            }
            itensValidados.add(Map.of(
                    "tipo", tipo,
                    "id", item.path("id").intValue(),
                    "quantidade", item.path("quantidade").intValue()));
        }
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "clienteId", clienteId.intValue(),
                    "formaPagamento", formaPagamento.asText(),
                    "itens", itensValidados));
        } catch (JacksonException exception) {
            throw new BusinessException("Não foi possível preparar o atendimento para confirmação");
        }
    }

    private String executarAtendimento(String rascunho) {
        try {
            JsonNode args = objectMapper.readTree(rascunho);
            Map<String, Object> payload = Map.of(
                    "clienteId", args.path("clienteId").intValue(),
                    "formaPagamento", args.path("formaPagamento").asText(),
                    "itens", objectMapper.convertValue(
                            args.path("itens"),
                            new TypeReference<List<Map<String, Object>>>() {}));
            String result = internalClient.post()
                    .uri("/atendimentos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> headers.setBasicAuth(internalUser, internalPassword))
                    .body(payload)
                    .retrieve()
                    .body(String.class);
            JsonNode atendimento = result == null ? null : objectMapper.readTree(result);
            if (atendimento == null || !atendimento.path("id").isIntegralNumber()) {
                throw new BusinessException("Resposta inválida ao criar atendimento");
            }
            return "criar_atendimento#" + atendimento.path("id").asInt();
        } catch (JacksonException exception) {
            throw new BusinessException("Rascunho inválido para criar atendimento");
        } catch (RestClientResponseException exception) {
            throw new BusinessException("Falha ao criar atendimento: "
                    + exception.getStatusCode().value() + " " + exception.getResponseBodyAsString());
        } catch (RestClientException exception) {
            throw new BusinessException("Falha ao criar atendimento: " + exception.getMessage());
        }
    }

    private String uriComLimite(String path, int limite) {
        return UriComponentsBuilder.fromPath(path)
                .queryParam("limite", limite)
                .build()
                .toUriString();
    }

    private String uriComNome(String path, String nome) {
        if (nome.isBlank()) {
            throw new BusinessException("O nome para busca não pode estar vazio");
        }
        return UriComponentsBuilder.fromPath(path)
                .queryParam("nome", nome)
                .build()
                .encode()
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
        definicoes.add(tool("buscar_cliente_por_nome",
                "Busca clientes por nome (correspondência parcial, sem diferenciar maiúsculas/minúsculas).",
                Map.of("nome", propriedade("string", "Nome ou parte do nome do cliente."))));
        definicoes.add(tool("buscar_produto_por_nome",
                "Busca produtos ativos por nome (correspondência parcial, sem diferenciar maiúsculas/minúsculas).",
                Map.of("nome", propriedade("string", "Nome ou parte do nome do produto."))));
        definicoes.add(tool("buscar_servico_por_nome",
                "Busca serviços por nome (correspondência parcial, sem diferenciar maiúsculas/minúsculas).",
                Map.of("nome", propriedade("string", "Nome ou parte do nome do serviço."))));
        definicoes.add(tool("listar_clientes_recentes",
                "Lista clientes cadastrados no sistema.",
                Map.of()));
        definicoes.add(tool("listar_formas_pagamento",
                "Lista as formas de pagamento aceitas: pix, dinheiro, cartao_credito e cartao_debito.",
                Map.of()));
        definicoes.add(toolComObrigatorios(
                "criar_atendimento",
                "Prepara um rascunho de atendimento para confirmação explícita. Não cria o atendimento por conta própria.",
                Map.of(
                        "clienteId", propriedade("integer", "Identificador do cliente."),
                        "itens", Map.of(
                                "type", "array",
                                "items", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "tipo", propriedade("string", "produto ou servico."),
                                                "id", propriedade("integer", "Identificador do produto ou serviço."),
                                                "quantidade", propriedade("integer", "Quantidade positiva.")),
                                        "required", List.of("tipo", "id", "quantidade"))),
                        "formaPagamento", Map.of(
                                "type", "string",
                                "enum", List.of("pix", "dinheiro", "cartao_credito", "cartao_debito"))),
                List.of("clienteId", "itens", "formaPagamento")));
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

    private Map<String, Object> toolComObrigatorios(String nome, String descricao,
                                                     Map<String, Object> propriedades,
                                                     List<String> obrigatorios) {
        Map<String, Object> parameters = Map.of(
                "type", "object",
                "properties", propriedades,
                "required", obrigatorios);
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
