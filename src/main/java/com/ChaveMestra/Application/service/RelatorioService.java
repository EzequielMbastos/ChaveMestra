package com.ChaveMestra.Application.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ChaveMestra.Application.model.Atendimento;
import com.ChaveMestra.Application.model.Cliente;
import com.ChaveMestra.Application.model.Estoque;
import com.ChaveMestra.Application.model.MovimentoFinanceiro;
import com.ChaveMestra.Application.repository.AtendimentoRepository;
import com.ChaveMestra.Application.repository.ClienteRepository;
import com.ChaveMestra.Application.repository.EstoqueRepository;
import com.ChaveMestra.Application.repository.MovimentoFinanceiroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private final EstoqueRepository estoqueRepository;
    private final ClienteRepository clienteRepository;
    private final MovimentoFinanceiroRepository movimentoFinanceiroRepository;
    private final AtendimentoRepository atendimentoRepository;

    public RelatorioService(EstoqueRepository estoqueRepository,
                            ClienteRepository clienteRepository,
                            MovimentoFinanceiroRepository movimentoFinanceiroRepository,
                            AtendimentoRepository atendimentoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.clienteRepository = clienteRepository;
        this.movimentoFinanceiroRepository = movimentoFinanceiroRepository;
        this.atendimentoRepository = atendimentoRepository;
    }

    @Transactional(readOnly = true)
    public PeriodoFinanceiroRelatorio relatorioFinanceiro(LocalDate inicio, LocalDate fim) {
        List<MovimentoFinanceiro> movimentos = movimentoFinanceiroRepository.findAll().stream()
                .filter(movimento -> dentroDoPeriodo(movimento.getVencimento(), inicio, fim))
                .toList();

        BigDecimal entradas = somarPorTipo(movimentos, "entrada");
        BigDecimal saidas = somarPorTipo(movimentos, "saida");
        long atendimentos = atendimentoRepository.findAll().stream()
                .filter(atendimento -> dentroDoPeriodo(
                        atendimento.getData() != null ? atendimento.getData().toLocalDate() : null,
                        inicio,
                        fim))
                .count();

        List<MovimentoPeriodoItem> itens = movimentos.stream()
                .map(this::paraItemPeriodo)
                .toList();

        return new PeriodoFinanceiroRelatorio(
                entradas,
                saidas,
                entradas.subtract(saidas),
                atendimentos,
                itens
        );
    }

    @Transactional(readOnly = true)
    public ProdutosBaixoEstoqueRelatorio produtosBaixoEstoque(int limite) {
        List<ProdutoBaixoEstoque> produtos = estoqueRepository.findAll().stream()
                .filter(estoque -> estoque.getProduto() != null)
                .filter(estoque -> Boolean.TRUE.equals(estoque.getProduto().getAtivo()))
                .filter(estoque -> estoque.getQuantidade() < limite)
                .map(estoque -> new ProdutoBaixoEstoque(
                        estoque.getProduto().getId(),
                        estoque.getProduto().getNome(),
                        estoque.getQuantidade()))
                .toList();

        return new ProdutosBaixoEstoqueRelatorio(produtos.size(), produtos);
    }

    @Transactional(readOnly = true)
    public List<ClientesPorEstado> clientesPorEstado() {
        Map<String, Long> totais = clienteRepository.findAll().stream()
                .map(Cliente::getEstado)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(estado -> !estado.isEmpty())
                .map(estado -> estado.toUpperCase(Locale.ROOT))
                .collect(Collectors.groupingBy(estado -> estado, Collectors.counting()));

        return totais.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ClientesPorEstado(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Transactional(readOnly = true)
    public FinanceiroResumo financeiroResumo() {
        YearMonth mesAtual = YearMonth.now();
        List<MovimentoFinanceiro> movimentos = movimentoFinanceiroRepository.findAll().stream()
                .filter(movimento -> movimento.getDataMovimento() != null)
                .filter(movimento -> YearMonth.from(movimento.getDataMovimento()).equals(mesAtual))
                .toList();

        BigDecimal entradas = somarPorTipo(movimentos, "entrada");
        BigDecimal saidas = somarPorTipo(movimentos, "saida");

        return new FinanceiroResumo(
                entradas,
                saidas,
                entradas.subtract(saidas),
                mesAtual.toString()
        );
    }

    @Transactional(readOnly = true)
    public List<AtendimentoRecente> atendimentosRecentes(int limite) {
        return atendimentoRepository.findAll().stream()
                .sorted(Comparator.comparing(
                        Atendimento::getData,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(Math.max(0, limite))
                .map(atendimento -> new AtendimentoRecente(
                        atendimento.getId(),
                        atendimento.getData(),
                        atendimento.getCliente() != null ? atendimento.getCliente().getNome() : null,
                        atendimento.getValorTotal(),
                        atendimento.getFormaPagamento()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EstoqueCritico> estoqueCritico() {
        return estoqueRepository.findAll().stream()
                .filter(estoque -> estoque.getProduto() != null)
                .filter(estoque -> Boolean.TRUE.equals(estoque.getProduto().getAtivo()))
                .filter(estoque -> estoque.getQuantidade() <= estoque.getMinimo())
                .map(estoque -> new EstoqueCritico(
                        estoque.getProduto().getId(),
                        estoque.getProduto().getNome(),
                        estoque.getQuantidade(),
                        estoque.getMinimo()))
                .toList();
    }

    private BigDecimal somarPorTipo(List<MovimentoFinanceiro> movimentos, String tipoEsperado) {
        return movimentos.stream()
                .filter(movimento -> movimento.getCategoria() != null)
                .filter(movimento -> tipoFinanceiro(movimento).equals(tipoEsperado))
                .map(movimento -> movimento.getValor() != null ? movimento.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String tipoFinanceiro(MovimentoFinanceiro movimento) {
        if (movimento.getCategoria() == null || movimento.getCategoria().getTipo() == null) {
            return "";
        }
        String tipo = movimento.getCategoria().getTipo().toLowerCase(Locale.ROOT);
        return switch (tipo) {
            case "entrada", "receita" -> "entrada";
            case "saida", "saída", "despesa" -> "saida";
            default -> "";
        };
    }

    private MovimentoPeriodoItem paraItemPeriodo(MovimentoFinanceiro movimento) {
        String tipo = tipoFinanceiro(movimento);
        return new MovimentoPeriodoItem(
                "entrada".equals(tipo) ? "ENTRADA" : "SAIDA",
                movimento.getCategoria() != null ? movimento.getCategoria().getNome() : null,
                movimento.getNome(),
                movimento.getDescricao(),
                movimento.getVencimento(),
                movimento.getValor(),
                movimento.getStatus() != null
                        ? movimento.getStatus().toUpperCase(Locale.ROOT)
                        : null
        );
    }

    private boolean dentroDoPeriodo(LocalDate data, LocalDate inicio, LocalDate fim) {
        return data != null
                && (inicio == null || !data.isBefore(inicio))
                && (fim == null || !data.isAfter(fim));
    }

    public record PeriodoFinanceiroRelatorio(
            @JsonProperty("total_entradas") BigDecimal totalEntradas,
            @JsonProperty("total_saidas") BigDecimal totalSaidas,
            BigDecimal saldo,
            long atendimentos,
            List<MovimentoPeriodoItem> itens
    ) {}

    public record MovimentoPeriodoItem(
            String tipo,
            String categoria,
            String pessoa,
            String descricao,
            @JsonProperty("data_vencimento") LocalDate dataVencimento,
            BigDecimal valor,
            String status
    ) {}

    public record ProdutosBaixoEstoqueRelatorio(
            int total,
            List<ProdutoBaixoEstoque> produtos
    ) {}

    public record ProdutoBaixoEstoque(
            Integer id,
            String nome,
            Integer quantidadeEstoque
    ) {}

    public record ClientesPorEstado(String estado, long total) {}

    public record FinanceiroResumo(
            BigDecimal entradas,
            BigDecimal saidas,
            BigDecimal saldo,
            String mes
    ) {}

    public record AtendimentoRecente(
            Integer id,
            LocalDateTime data,
            String clienteNome,
            BigDecimal valorTotal,
            String formaPagamento
    ) {}

    public record EstoqueCritico(
            Integer id,
            String nome,
            Integer quantidadeEstoque,
            Integer minimo
    ) {}
}
