package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.dto.AtendimentoItemRequest;
import com.ChaveMestra.Application.dto.AtendimentoItemResponse;
import com.ChaveMestra.Application.mapper.AtendimentoItemMapper;
import com.ChaveMestra.Application.model.Atendimento;
import com.ChaveMestra.Application.model.AtendimentoItem;
import com.ChaveMestra.Application.model.Produto;
import com.ChaveMestra.Application.model.Servico;
import com.ChaveMestra.Application.repository.AtendimentoItemRepository;
import com.ChaveMestra.Application.repository.AtendimentoRepository;
import com.ChaveMestra.Application.repository.ProdutoRepository;
import com.ChaveMestra.Application.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AtendimentoItemService {

    private final AtendimentoItemRepository itemRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final ProdutoRepository produtoRepository;
    private final ServicoRepository servicoRepository;
    private final AtendimentoItemMapper itemMapper;

    public AtendimentoItemService(AtendimentoItemRepository itemRepository,
                                  AtendimentoRepository atendimentoRepository,
                                  ProdutoRepository produtoRepository,
                                  ServicoRepository servicoRepository,
                                  AtendimentoItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.produtoRepository = produtoRepository;
        this.servicoRepository = servicoRepository;
        this.itemMapper = itemMapper;
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

        Produto produto = null;
        Servico servico = null;

        if ("produto".equalsIgnoreCase(request.tipo())) {
            if (request.produtoId() == null) {
                throw new RuntimeException("produtoId é obrigatório quando tipo = 'produto'");
            }
            produto = produtoRepository.findById(request.produtoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        } else if ("servico".equalsIgnoreCase(request.tipo())) {
            if (request.servicoId() == null) {
                throw new RuntimeException("servicoId é obrigatório quando tipo = 'servico'");
            }
            servico = servicoRepository.findById(request.servicoId())
                    .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        } else {
            throw new RuntimeException("tipo deve ser 'produto' ou 'servico'");
        }

        AtendimentoItem item = itemMapper.toEntity(
                atendimento, produto, servico,
                request.quantidade(), request.valorUnitario(),
                request.desconto(), request.tipo(), request.observacao()
        );

        AtendimentoItem salvo = itemRepository.save(item);
        return itemMapper.toResponse(salvo);
    }

    @Transactional
    public AtendimentoItemResponse atualizar(Integer id, AtendimentoItemRequest request) {
        AtendimentoItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item de atendimento não encontrado"));

        Atendimento atendimento = atendimentoRepository.findById(request.atendimentoId())
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));

        Produto produto = null;
        Servico servico = null;

        if ("produto".equalsIgnoreCase(request.tipo())) {
            if (request.produtoId() == null) {
                throw new RuntimeException("produtoId é obrigatório quando tipo = 'produto'");
            }
            produto = produtoRepository.findById(request.produtoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        } else if ("servico".equalsIgnoreCase(request.tipo())) {
            if (request.servicoId() == null) {
                throw new RuntimeException("servicoId é obrigatório quando tipo = 'servico'");
            }
            servico = servicoRepository.findById(request.servicoId())
                    .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        } else {
            throw new RuntimeException("tipo deve ser 'produto' ou 'servico'");
        }

        AtendimentoItem novosDados = itemMapper.toEntity(
                atendimento, produto, servico,
                request.quantidade(), request.valorUnitario(),
                request.desconto(), request.tipo(), request.observacao()
        );

        item.setAtendimento(novosDados.getAtendimento());
        item.setProduto(novosDados.getProduto());
        item.setServico(novosDados.getServico());
        item.setQuantidade(novosDados.getQuantidade());
        item.setValorUnitario(novosDados.getValorUnitario());
        item.setDesconto(novosDados.getDesconto());
        item.setValorTotal(novosDados.getValorTotal());
        item.setTipo(novosDados.getTipo());
        item.setObservacao(novosDados.getObservacao());

        AtendimentoItem atualizado = itemRepository.save(item);
        return itemMapper.toResponse(atualizado);
    }

    @Transactional
    public void excluir(Integer id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item de atendimento não encontrado");
        }
        itemRepository.deleteById(id);
    }
}