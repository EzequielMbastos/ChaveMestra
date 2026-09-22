package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.model.Categoria;
import com.ChaveMestra.Application.model.Fornecedor;
import com.ChaveMestra.Application.model.Produto;
import com.ChaveMestra.Application.repository.CategoriaRepository;
import com.ChaveMestra.Application.repository.FornecedorRepository;
import com.ChaveMestra.Application.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FornecedorRepository fornecedorRepository;

    public ProdutoService(ProdutoRepository produtoRepository,
                          CategoriaRepository categoriaRepository,
                          FornecedorRepository fornecedorRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    public Produto cadastrar(Produto produto) {
        // Busca a categoria REAL do banco — não confia no objeto do request
        Categoria categoria = categoriaRepository.findById(produto.getCategoria().getId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        produto.setCategoria(categoria);

        // Busca o fornecedor se vier preenchido
        if (produto.getFornecedor() != null && produto.getFornecedor().getId() != null) {
            Fornecedor fornecedor = fornecedorRepository.findById(produto.getFornecedor().getId())
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
            produto.setFornecedor(fornecedor);
        }

        return produtoRepository.save(produto);
    }

    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(Integer id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public Produto atualizar(Integer id, Produto dados) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Busca a categoria REAL do banco
        Categoria categoria = categoriaRepository.findById(dados.getCategoria().getId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        // Busca o fornecedor (opcional)
        Fornecedor fornecedor = null;
        if (dados.getFornecedor() != null && dados.getFornecedor().getId() != null) {
            fornecedor = fornecedorRepository.findById(dados.getFornecedor().getId())
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        }

        produto.setCodigoCatalogo(dados.getCodigoCatalogo());
        produto.setNome(dados.getNome());
        produto.setPrecoVenda(dados.getPrecoVenda());
        produto.setPrecoCusto(dados.getPrecoCusto());
        produto.setCategoria(categoria);       // objeto gerenciado pelo Hibernate
        produto.setFornecedor(fornecedor);

        return produtoRepository.save(produto);
    }

    public void excluir(Integer id) {
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado");
        }
        produtoRepository.deleteById(id);
    }
}