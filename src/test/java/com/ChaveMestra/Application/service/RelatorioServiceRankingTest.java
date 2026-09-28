package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.model.AtendimentoItem;
import com.ChaveMestra.Application.model.Produto;
import com.ChaveMestra.Application.repository.AtendimentoItemRepository;
import com.ChaveMestra.Application.repository.AtendimentoRepository;
import com.ChaveMestra.Application.repository.ClienteRepository;
import com.ChaveMestra.Application.repository.EstoqueRepository;
import com.ChaveMestra.Application.repository.MovimentoFinanceiroRepository;
import com.ChaveMestra.Application.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceRankingTest {

    @Mock
    private EstoqueRepository estoqueRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private MovimentoFinanceiroRepository movimentoFinanceiroRepository;
    @Mock
    private AtendimentoRepository atendimentoRepository;
    @Mock
    private AtendimentoItemRepository atendimentoItemRepository;
    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    @Test
    void produtosMaisVendidosAgrupaQuantidadeReceitaEAplicaLimite() {
        Produto copia = produto(1, "Cópia de chave");
        Produto cadeado = produto(2, "Cadeado");
        when(atendimentoItemRepository.findAll()).thenReturn(List.of(
                item(copia, 2, "10.00"),
                item(copia, 3, "12.00"),
                item(cadeado, 1, "30.00")));

        List<RelatorioService.ProdutoMaisVendido> ranking = relatorioService.produtosMaisVendidos(1);

        assertEquals(1, ranking.size());
        assertEquals(1, ranking.get(0).produtoId());
        assertEquals(5, ranking.get(0).quantidadeTotalVendida());
        assertEquals(new BigDecimal("56.00"), ranking.get(0).receitaTotal());
    }

    @Test
    void produtosMenosVendidosOrdenaAscendente() {
        Produto copia = produto(1, "Cópia de chave");
        Produto cadeado = produto(2, "Cadeado");
        when(atendimentoItemRepository.findAll()).thenReturn(List.of(
                item(copia, 2, "10.00"),
                item(cadeado, 1, "30.00")));

        List<RelatorioService.ProdutoMaisVendido> ranking = relatorioService.produtosMenosVendidos(10);

        assertEquals(List.of(2, 1), ranking.stream().map(RelatorioService.ProdutoMaisVendido::produtoId).toList());
    }

    @Test
    void produtosMenosVendidosIncluiProdutosAtivosSemVendas() {
        Produto semVendas = produto(3, "Chaveiro sem vendas");
        when(atendimentoItemRepository.findAll()).thenReturn(List.of());
        when(produtoRepository.findByAtivoTrue()).thenReturn(List.of(semVendas));

        List<RelatorioService.ProdutoMaisVendido> ranking = relatorioService.produtosMenosVendidos(10);

        assertEquals(1, ranking.size());
        assertEquals(3, ranking.get(0).produtoId());
        assertEquals(0, ranking.get(0).quantidadeTotalVendida());
        assertEquals(BigDecimal.ZERO, ranking.get(0).receitaTotal());
    }

    private Produto produto(int id, String nome) {
        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome(nome);
        return produto;
    }

    private AtendimentoItem item(Produto produto, int quantidade, String valorUnitario) {
        AtendimentoItem item = new AtendimentoItem();
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setValorUnitario(new BigDecimal(valorUnitario));
        return item;
    }
}
