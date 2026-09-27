# ChaveMestra

Sistema de gestão para chaveiros e prestadores de serviços, com módulos de atendimento, produtos, serviços, clientes, estoque, financeiro e assistente de IA integrado.

## ✨ Funcionalidades

- **Atendimentos**: registro de vendas de produtos e serviços com múltiplas formas de pagamento
- **Produtos e Serviços**: CRUD completo com soft delete
- **Clientes**: cadastro com endereço e estado
- **Estoque**: controle de quantidade e estoque mínimo
- **Financeiro**: movimentações de entrada/saída, categorias e resumo mensal
- **Relatórios**: produtos com baixo estoque, clientes por estado, saldo financeiro, atendimentos recentes
- **Assistente IA**: chat em linguagem natural que consulta dados reais via tool calling (OpenRouter + DeepSeek)

## 🛠️ Stack Tecnológica

| Camada | Tecnologia |
| :--- | :--- |
| Linguagem | Java 21 |
| Framework | Spring Boot 4.1.1 (Web MVC, Data JPA, Security, Validation) |
| Banco | PostgreSQL 17 (Supabase) |
| Migrations | Flyway |
| Frontend | HTML + Bootstrap 5 + JavaScript (vanilla) |
| IA | OpenRouter (modelo deepseek/deepseek-chat) com tool calling |
| Build | Maven |
| Container | Docker + Docker Compose |

## 📋 Pré-requisitos

- Java 21 (Temurin ou similar)
- Maven 3.9+
- Docker e Docker Compose (opcional, mas recomendado)
- Conta no [Supabase](https://supabase.com) com banco PostgreSQL
- Chave de API do [OpenRouter](https://openrouter.ai/keys)

## 🚀 Como rodar

### Opção 1: Com Docker (recomendado)

1. Clone o repositório:
   ```bash
   git clone https://github.com/EzequielMbastos/ChaveMestra.git
   cd ChaveMestra
   ```
2. Crie um arquivo `.env` na raiz com as credenciais do banco e a chave OpenRouter:
   ```dotenv
   SPRING_DATASOURCE_PASSWORD=sua-senha-do-banco
   OPENROUTER_API_KEY=sua-chave-openrouter
   ```
   Opcionalmente, configure `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` ou `OPENROUTER_MODEL` no `.env`; o Compose encaminha essas opções para a aplicação.
3. Inicie a aplicação:
   ```bash
   docker compose up --build -d
   ```
4. Acesse `http://localhost:8080`. Para acompanhar os logs:
   ```bash
   docker compose logs -f app
   ```
5. Para parar:
   ```bash
   docker compose down
   ```

### Opção 2: Localmente com Maven

1. Configure as variáveis de ambiente antes de iniciar:
   ```bash
   export SPRING_DATASOURCE_URL='jdbc:postgresql://<host>:5432/<database>?sslmode=require'
   export SPRING_DATASOURCE_USERNAME='<usuario>'
   export SPRING_DATASOURCE_PASSWORD='<senha>'
   export OPENROUTER_API_KEY='<chave-openrouter>'
   ```
   `OPENROUTER_MODEL` (padrão `deepseek/deepseek-chat`) e `OPENROUTER_PROVIDER_ORDER` (padrão `DeepInfra,Together,Fireworks`) são opcionais. A ordem de provedores também pode ser ajustada em `application.properties`.
2. Inicie o backend:
   ```bash
   mvn spring-boot:run
   ```
3. Acesse `http://localhost:8080`.

As migrações do banco são aplicadas pelo Flyway na inicialização. Mantenha credenciais e chaves de API fora do controle de versão.

## 🗂️ Estrutura do projeto

- `src/main/java/com/ChaveMestra/Application/`: aplicação Spring, organizada em controllers, services, repositories, models, DTOs, mappers, configuração e tratamento de exceções.
- `src/main/resources/db/migration/`: migrações versionadas do banco de dados.
- `src/main/resources/static/`: páginas HTML, estilos e scripts do frontend.
- `docs/adr/`: registros das decisões de arquitetura e template para novas ADRs.
