package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.dto.EstoqueRequest;
import com.ChaveMestra.Application.dto.EstoqueResponse;
import com.ChaveMestra.Application.exception.BusinessException;
import com.ChaveMestra.Application.exception.ResourceNotFoundException;
import com.ChaveMestra.Application.mapper.EstoqueMapper;
import com.ChaveMestra.Application.model.Estoque;
import com.ChaveMestra.Application.model.Produto;
import com.ChaveMestra.Application.repository.EstoqueRepository;
import com.ChaveMestra.Application.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueMapper estoqueMapper;

    public EstoqueService(EstoqueRepository estoqueRepository,
                          ProdutoRepository produtoRepository,
                          EstoqueMapper estoqueMapper) {
        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueMapper = estoqueMapper;
    }

    @Transactional(readOnly = true)
    public List<EstoqueResponse> listar() {
        return estoqueRepository.findAll()
                .stream()
                .map(estoqueMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstoqueResponse buscarPorId(Integer id) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado"));
        return estoqueMapper.toResponse(estoque);
    }

    @Transactional
    public EstoqueResponse cadastrar(EstoqueRequest request) {
        if (request.quantidade() < 0 || request.minimo() < 0) {
            throw new BusinessException("Quantidade e mínimo do estoque não podem ser negativos");
        }
        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        if (estoqueRepository.findByProdutoId(produto.getId()).isPresent()) {
            throw new BusinessException("Já existe estoque para este produto");
        }

        Estoque estoque = estoqueMapper.toEntity(request, produto);
        Estoque salvo = estoqueRepository.save(estoque);
        return estoqueMapper.toResponse(salvo);
    }

    @Transactional
    public EstoqueResponse atualizar(Integer id, EstoqueRequest request) {
        if (request.quantidade() < 0 || request.minimo() < 0) {
            throw new BusinessException("Quantidade e mínimo do estoque não podem ser negativos");
        }
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado"));

        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        // Só valida duplicidade se o produto mudou
        if (!estoque.getProduto().getId().equals(produto.getId())) {
            if (estoqueRepository.findByProdutoId(produto.getId()).isPresent()) {
                throw new BusinessException("Já existe estoque para este produto");
            }
        }

        estoque.setProduto(produto);
        estoque.setQuantidade(request.quantidade());
        estoque.setMinimo(request.minimo());

        Estoque atualizado = estoqueRepository.save(estoque);
        return estoqueMapper.toResponse(atualizado);
    }

    @Transactional
    public void excluir(Integer id) {
        if (!estoqueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Estoque não encontrado");
        }
        estoqueRepository.deleteById(id);
    }
}