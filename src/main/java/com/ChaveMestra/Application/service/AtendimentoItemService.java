package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.dto.AtendimentoItemRequest;
import com.ChaveMestra.Application.dto.AtendimentoItemResponse;
import com.ChaveMestra.Application.exception.BusinessException;
import com.ChaveMestra.Application.exception.ResourceNotFoundException;
import com.ChaveMestra.Application.mapper.AtendimentoItemMapper;
import com.ChaveMestra.Application.model.Atendimento;
import com.ChaveMestra.Application.model.AtendimentoItem;
import com.ChaveMestra.Application.model.Estoque;
import com.ChaveMestra.Application.model.Produto;
import com.ChaveMestra.Application.model.Servico;
import com.ChaveMestra.Application.repository.AtendimentoItemRepository;
import com.ChaveMestra.Application.repository.AtendimentoRepository;
import com.ChaveMestra.Application.repository.EstoqueRepository;
import com.ChaveMestra.Application.repository.ProdutoRepository;
import com.ChaveMestra.Application.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AtendimentoItemService {

    private final AtendimentoItemRepository itemRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final ProdutoRepository produtoRepository;
    private final ServicoRepository servicoRepository;
    private final EstoqueRepository estoqueRepository;
    private final AtendimentoItemMapper itemMapper;
    private final AtendimentoService atendimentoService;

    public AtendimentoItemService(AtendimentoItemRepository itemRepository,
                                  AtendimentoRepository atendimentoRepository,
                                  ProdutoRepository produtoRepository,
                                  ServicoRepository servicoRepository,
                                  EstoqueRepository estoqueRepository,
                                  AtendimentoItemMapper itemMapper,
                                  AtendimentoService atendimentoService) {
        this.itemRepository = itemRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.produtoRepository = produtoRepository;
        this.servicoRepository = servicoRepository;
        this.estoqueRepository = estoqueRepository;
        this.itemMapper = itemMapper;
        this.atendimentoService = atendimentoService;
    }

    @Transactional(readOnly = true)
    public List<AtendimentoItemResponse> listar() {
        return itemRepository.findAll()
                .stream()
                .map(itemMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AtendimentoItemResponse buscarPorId(Integer id) {
        AtendimentoItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item de atendimento não encontrado"));
        return itemMapper.toResponse(item);
    }

    @Transactional
    public AtendimentoItemResponse cadastrar(AtendimentoItemRequest request) {
        Atendimento atendimento = atendimentoRepository.findById(request.atendimentoId())
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));

        AtendimentoItem item = new AtendimentoItem();
        item.setAtendimento(atendimento);
        item.setQuantidade(request.quantidade());
        item.setDesconto(request.desconto());
        item.setTipo(request.tipo());
        item.setObservacao(request.observacao());

        if ("produto".equalsIgnoreCase(request.tipo())) {
            if (request.produtoId() == null) {
                throw new RuntimeException("produtoId é obrigatório quando tipo = 'produto'");
            }
            item.setServico(null);
            item.setProduto(produtoRepository.findById(request.produtoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado")));
        } else if ("servico".equalsIgnoreCase(request.tipo())) {
            if (request.servicoId() == null) {
                throw new RuntimeException("servicoId é obrigatório quando tipo = 'servico'");
            }
            item.setProduto(null);
            item.setServico(servicoRepository.findById(request.servicoId())
                    .orElseThrow(() -> new RuntimeException("Serviço não encontrado")));
        } else {
            throw new BusinessException("tipo deve ser 'produto' ou 'servico'");
        }

        processarItem(item);

        AtendimentoItem salvo = itemRepository.save(item);
        atendimentoService.recalcularValorTotal(request.atendimentoId());
        return itemMapper.toResponse(salvo);
    }

    @Transactional
    public AtendimentoItemResponse atualizar(Integer id, AtendimentoItemRequest request) {
        AtendimentoItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item de atendimento não encontrado"));

        if (!item.getAtendimento().getId().equals(request.atendimentoId())) {
            throw new BusinessException("Não é permitido transferir um item entre atendimentos");
        }

        Atendimento atendimento = atendimentoRepository.findById(request.atendimentoId())
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));

        // Se o item existente era produto, devolver a quantidade ao estoque antes de atualizar
        if ("produto".equalsIgnoreCase(item.getTipo()) && item.getProduto() != null) {
            estoqueRepository.adicionarEstoque(item.getProduto().getId(), item.getQuantidade());
        }

        item.setAtendimento(atendimento);
        item.setQuantidade(request.quantidade());
        item.setDesconto(request.desconto());
        item.setTipo(request.tipo());
        item.setObservacao(request.observacao());

        if ("produto".equalsIgnoreCase(request.tipo())) {
            if (request.produtoId() == null) {
                throw new RuntimeException("produtoId é obrigatório quando tipo = 'produto'");
            }
            item.setServico(null);
            item.setProduto(produtoRepository.findById(request.produtoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado")));
        } else if ("servico".equalsIgnoreCase(request.tipo())) {
            if (request.servicoId() == null) {
                throw new RuntimeException("servicoId é obrigatório quando tipo = 'servico'");
            }
            item.setProduto(null);
            item.setServico(servicoRepository.findById(request.servicoId())
                    .orElseThrow(() -> new RuntimeException("Serviço não encontrado")));
        } else {
            throw new BusinessException("tipo deve ser 'produto' ou 'servico'");
        }

        processarItem(item);

        AtendimentoItem atualizado = itemRepository.save(item);
        atendimentoService.recalcularValorTotal(request.atendimentoId());
        return itemMapper.toResponse(atualizado);
    }

    @Transactional
    public void excluir(Integer id) {
        AtendimentoItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item de atendimento não encontrado"));

        Integer atendimentoId = item.getAtendimento().getId();

        // Se for produto, devolver a quantidade ao estoque
        if ("produto".equalsIgnoreCase(item.getTipo()) && item.getProduto() != null) {
            estoqueRepository.adicionarEstoque(item.getProduto().getId(), item.getQuantidade());
        }

        itemRepository.deleteById(id);
        atendimentoService.recalcularValorTotal(atendimentoId);
    }

    private void processarItem(AtendimentoItem item) {
        boolean produtoSelecionado = "produto".equalsIgnoreCase(item.getTipo());
        boolean servicoSelecionado = "servico".equalsIgnoreCase(item.getTipo());

        if (!produtoSelecionado && !servicoSelecionado) {
            throw new BusinessException("tipo deve ser 'produto' ou 'servico'");
        }
        if (produtoSelecionado && item.getProduto() == null) {
            throw new ResourceNotFoundException("Produto não encontrado");
        }
        if (servicoSelecionado && item.getServico() == null) {
            throw new ResourceNotFoundException("Serviço não encontrado");
        }

        BigDecimal valorUnitario;
        if (produtoSelecionado) {
            valorUnitario = item.getProduto().getPrecoVenda();
            Estoque estoque = estoqueRepository.findByProdutoId(item.getProduto().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Estoque não encontrado para este produto"));

            if (estoque.getQuantidade() < item.getQuantidade()) {
                throw new BusinessException("Estoque insuficiente. Disponível: "
                        + estoque.getQuantidade() + ", solicitado: " + item.getQuantidade());
            }
            estoqueRepository.baixarEstoque(item.getProduto().getId(), item.getQuantidade());
        } else {
            valorUnitario = item.getServico().getPrecoBase();
        }

        BigDecimal desconto = item.getDesconto() != null
                ? item.getDesconto()
                : BigDecimal.ZERO;
        item.setDesconto(desconto);
        item.setValorUnitario(valorUnitario);
        item.setValorTotal(valorUnitario
                .multiply(BigDecimal.valueOf(item.getQuantidade()))
                .subtract(desconto));
    }
}