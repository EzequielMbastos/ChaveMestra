# ADR-001: Integração de IA no Backend com Tool Calling

**Status:** Aceita  
**Data:** 2026-09-27  
**Contexto do projeto:** ChaveMestra

---

## Contexto

O ChaveMestra precisava de um assistente de IA capaz de responder perguntas de negócio em linguagem natural ("Quantos clientes temos em SP?", "Qual o saldo do mês?"). A interface existia no frontend, mas chamava o OpenRouter diretamente do navegador, gerando 4 problemas críticos:

1. **Segurança**: a API key ficava no localStorage, vulnerável a XSS
2. **Sem acesso a dados**: a IA só respondia com o que o usuário colava
3. **Sem auditoria**: nenhum registro das interações
4. **Sem evolução**: impossível permitir que a IA executasse ações

---

## Decisões

### 1. Chamada de IA no backend
**Decisão:** A IA é chamada pelo backend (`IaService`), não pelo frontend.  
**Justificativa:** segurança da chave, auditoria, acesso a dados, evolução.  
**Trade-offs:** latência adicional, configuração de env vars.

### 2. Usar OpenRouter em vez de provedor direto
**Decisão:** Usar OpenRouter como gateway.  
**Justificativa:** flexibilidade de modelos, API compatível com OpenAI, fallback automático, billing único.  
**Trade-offs:** dependência do OpenRouter, rate limits compartilhados.

### 3. Implementar Tool Calling
**Decisão:** 6 tools que a IA pode chamar para consultar dados reais.  
**Justificativa:** respostas factuais com dados do banco (não alucina).  
**Trade-offs:** custo extra de tokens, latência maior, limite de 5 iterações.

### 4. Usar RestClient (Spring 7)
**Decisão:** RestClient para chamadas HTTP.  
**Justificativa:** RestTemplate depreciado, WebClient é overkill.  
**Trade-offs:** menos familiar que RestTemplate.

### 5. Basic Auth para chamadas internas
**Decisão:** IaService → /relatorios/** via Basic Auth.  
**Justificativa:** passa pelo SecurityFilterChain, mantém auditoria.  
**Trade-offs:** credenciais em dev (configurável), latência local.

### 6. Registrar cada interação em ia_interacao
**Decisão:** pergunta + resposta + tempo + modelo vão para o banco.  
**Justificativa:** auditoria, controle de custos, melhoria contínua.  
**Trade-offs:** overhead de INSERT, tabela cresce rápido.

### 7. Loop de tool calling com limite de 5
**Decisão:** máximo 5 iterações por pergunta.  
**Justificativa:** proteção contra loop infinito, custo previsível.  
**Trade-offs:** perguntas muito complexas podem ser cortadas.

### 8. Configuração via placeholders
**Decisão:** API key e modelo via variáveis de ambiente.  
**Justificativa:** não vai para o Git, portável, 12-factor.  
**Trade-offs:** exige configuração inicial.

---

## Consequências

**Positivas:** IA segura, factual, auditável, evolutiva, base para agente.

**Negativas:** dependência do OpenRouter, latência, custo de tokens, gerenciamento de credenciais.

**Reversibilidade:** decisões 1-3 difíceis, 4-5 médias, 6-8 fáceis.

---

## Bugs resolvidos durante a implementação

1. **Jackson 3 vs 2**: pacote mudou de `com.fasterxml.jackson` para `tools.jackson` no Spring Boot 4.
2. **NUMERIC(5,2) → NUMERIC(10,2)**: `tempo_resposta` limitado a 999,99 ms.
3. **Constraint tipo_pergunta**: aceitava 'texto'/'voz', precisava incluir 'consulta'/'relatorio'/'outro'.
4. **Erro 429 (rate limit)**: resolvido com roteamento de provedor + retry.

---

## Referências

- OpenRouter API: https://openrouter.ai/docs
- OpenAI Function Calling: https://platform.openai.com/docs/guides/function-calling
- Spring RestClient: https://docs.spring.io/spring-framework/reference/integration/rest-clients.html
- 12-factor app: https://12factor.net/config

---

## Histórico

| Data | Versão | Descrição |
| :--- | :--- | :--- |
| 2026-09-27 | 1.0 | Criação inicial |
