package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.dto.AtendimentoRequest;
import com.ChaveMestra.Application.dto.AtendimentoResponse;
import com.ChaveMestra.Application.dto.AtendimentoItemCriacaoRequest;
import com.ChaveMestra.Application.mapper.AtendimentoMapper;
import com.ChaveMestra.Application.mapper.AtendimentoItemMapper;
import com.ChaveMestra.Application.model.Atendimento;
import com.ChaveMestra.Application.model.Cliente;
import com.ChaveMestra.Application.model.Produto;
import com.ChaveMestra.Application.model.Servico;
import com.ChaveMestra.Application.model.CategoriaFinanceira;
import com.ChaveMestra.Application.model.MovimentoFinanceiro;
import com.ChaveMestra.Application.repository.AtendimentoRepository;
import com.ChaveMestra.Application.repository.CategoriaFinanceiraRepository;
import com.ChaveMestra.Application.repository.ClienteRepository;
import com.ChaveMestra.Application.repository.MovimentoFinanceiroRepository;
import com.ChaveMestra.Application.repository.ProdutoRepository;
import com.ChaveMestra.Application.repository.ServicoRepository;
import com.ChaveMestra.Application.exception.BusinessException;
import com.ChaveMestra.Application.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final ClienteRepository clienteRepository;
    private final AtendimentoMapper atendimentoMapper;
    private final ProdutoRepository produtoRepository;
    private final ServicoRepository servicoRepository;
    private final AtendimentoItemMapper atendimentoItemMapper;
    private final CategoriaFinanceiraRepository categoriaFinanceiraRepository;
    private final MovimentoFinanceiroRepository movimentoFinanceiroRepository;

    public AtendimentoService(AtendimentoRepository atendimentoRepository,
                              ClienteRepository clienteRepository,
                              AtendimentoMapper atendimentoMapper,
                              ProdutoRepository produtoRepository,
                              ServicoRepository servicoRepository,
                              AtendimentoItemMapper atendimentoItemMapper,
                              CategoriaFinanceiraRepository categoriaFinanceiraRepository,
                              MovimentoFinanceiroRepository movimentoFinanceiroRepository) {
        this.atendimentoRepository = atendimentoRepository;
        this.clienteRepository = clienteRepository;
        this.atendimentoMapper = atendimentoMapper;
        this.produtoRepository = produtoRepository;
        this.servicoRepository = servicoRepository;
        this.atendimentoItemMapper = atendimentoItemMapper;
        this.categoriaFinanceiraRepository = categoriaFinanceiraRepository;
        this.movimentoFinanceiroRepository = movimentoFinanceiroRepository;
    }

    @Transactional(readOnly = true)
    public List<AtendimentoResponse> listar() {
        return atendimentoRepository.findAll()
                .stream()
                .map(atendimentoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AtendimentoResponse buscarPorId(Integer id) {
        Atendimento atendimento = atendimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));
        return atendimentoMapper.toResponse(atendimento);
    }

    @Transactional
    public AtendimentoResponse cadastrar(AtendimentoRequest request) {
        Cliente cliente = null;
        if (request.clienteId() != null) {
            cliente = clienteRepository.findById(request.clienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        }

        Atendimento atendimento = atendimentoMapper.toEntity(request, cliente);
        BigDecimal valorTotalItens = BigDecimal.ZERO;

        if (request.itens() != null) {
            for (AtendimentoItemCriacaoRequest itemRequest : request.itens()) {
                Produto produto = null;
                Servico servico = null;
                BigDecimal valorUnitario;

                if ("produto".equalsIgnoreCase(itemRequest.tipo())) {
                    produto = produtoRepository.findById(itemRequest.id())
                            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
                    valorUnitario = produto.getPrecoVenda();
                } else if ("servico".equalsIgnoreCase(itemRequest.tipo())) {
                    servico = servicoRepository.findById(itemRequest.id())
                            .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));
                    valorUnitario = servico.getPrecoBase();
                } else {
                    throw new BusinessException("tipo deve ser 'produto' ou 'servico'");
                }

                var item = atendimentoItemMapper.toEntity(
                        atendimento,
                        produto,
                        servico,
                        itemRequest.quantidade(),
                        valorUnitario,
                        itemRequest.desconto(),
                        itemRequest.tipo(),
                        itemRequest.observacao()
                );
                atendimento.adicionarItem(item);
                valorTotalItens = valorTotalItens.add(item.getValorTotal());
            }
        }

        atendimento.setValorTotal(valorTotalItens);
        atendimento.setValorLiquido(valorTotalItens.subtract(
                request.desconto() != null ? request.desconto() : BigDecimal.ZERO
        ));
        Atendimento salvo = atendimentoRepository.save(atendimento);

        CategoriaFinanceira categoriaReceita = categoriaFinanceiraRepository.findAll().stream()
                .filter(categoria -> "receita".equalsIgnoreCase(categoria.getTipo())
                        || "entrada".equalsIgnoreCase(categoria.getTipo()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Nenhuma categoria financeira de receita foi cadastrada"));

        MovimentoFinanceiro movimento = new MovimentoFinanceiro();
        movimento.setCategoria(categoriaReceita);
        movimento.setAtendimento(salvo);
        movimento.setNome("Venda");
        movimento.setDescricao("Atendimento #" + salvo.getId());
        movimento.setValor(salvo.getValorTotal());
        movimento.setDataMovimento(java.time.LocalDateTime.now());
        movimento.setVencimento(java.time.LocalDate.now());
        movimento.setStatus("pago");
        movimentoFinanceiroRepository.save(movimento);

        return atendimentoMapper.toResponse(salvo);
    }

    @Transactional
    public AtendimentoResponse atualizar(Integer id, AtendimentoRequest request) {
        Atendimento atendimento = atendimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));

        Cliente cliente = null;
        if (request.clienteId() != null) {
            cliente = clienteRepository.findById(request.clienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        }

        atendimento.setCliente(cliente);
        atendimento.setFormaPagamento(request.formaPagamento());
        atendimento.setDesconto(request.desconto() != null ? request.desconto() : BigDecimal.ZERO);
        atendimento.setObservacao(request.observacao());

        Atendimento atualizado = atendimentoRepository.save(atendimento);
        return atendimentoMapper.toResponse(atualizado);
    }

    @Transactional
    public void recalcularValorTotal(Integer atendimentoId) {
        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));

        BigDecimal valorTotal = atendimento.getItens().stream()
                .map(item -> item.getValorTotal() != null ? item.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        atendimento.setValorTotal(valorTotal);
        atendimentoRepository.save(atendimento);
    }

    @Transactional
    public void excluir(Integer id) {
        if (!atendimentoRepository.existsById(id)) {
            throw new RuntimeException("Atendimento não encontrado");
        }
        atendimentoRepository.deleteById(id);
    }
}