package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.dto.ProdutoRequest;
import com.ChaveMestra.Application.dto.ProdutoResponse;
import com.ChaveMestra.Application.exception.BusinessException;
import com.ChaveMestra.Application.exception.ResourceNotFoundException;
import com.ChaveMestra.Application.mapper.ProdutoMapper;
import com.ChaveMestra.Application.model.Categoria;
import com.ChaveMestra.Application.model.Fornecedor;
import com.ChaveMestra.Application.model.Produto;
import com.ChaveMestra.Application.repository.CategoriaRepository;
import com.ChaveMestra.Application.repository.FornecedorRepository;
import com.ChaveMestra.Application.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ProdutoMapper produtoMapper;

    public ProdutoService(ProdutoRepository produtoRepository,
                          CategoriaRepository categoriaRepository,
                          FornecedorRepository fornecedorRepository,
                          ProdutoMapper produtoMapper) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.produtoMapper = produtoMapper;
    }

    @Transactional
    public ProdutoResponse cadastrar(ProdutoRequest request) {
        Produto produto = produtoMapper.toEntity(request);
        produto.setAtivo(true);

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        produto.setCategoria(categoria);

        if (request.fornecedorId() != null) {
            Fornecedor fornecedor = fornecedorRepository.findById(request.fornecedorId())
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
            produto.setFornecedor(fornecedor);
        }

        Produto salvo = produtoRepository.save(produto);
        return produtoMapper.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar() {
        return produtoRepository.findByAtivoTrue()
                .stream()
                .map(produtoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Integer id) {
        Produto produto = produtoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        return produtoMapper.toResponse(produto);
    }

    @Transactional
    public ProdutoResponse atualizar(Integer id, ProdutoRequest request) {
        Produto produto = produtoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Fornecedor fornecedor = null;
        if (request.fornecedorId() != null) {
            fornecedor = fornecedorRepository.findById(request.fornecedorId())
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        }

        produto.setCodigoCatalogo(request.codigoCatalogo());
        produto.setNome(request.nome());
        produto.setPrecoVenda(request.precoVenda());
        produto.setPrecoCusto(request.precoCusto());
        produto.setCategoria(categoria);
        produto.setFornecedor(fornecedor);

        Produto atualizado = produtoRepository.save(produto);
        return produtoMapper.toResponse(atualizado);
    }

    @Transactional
    public void excluir(Integer id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        if (Boolean.FALSE.equals(produto.getAtivo())) {
            throw new BusinessException("Produto já está desativado");
        }
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }
}