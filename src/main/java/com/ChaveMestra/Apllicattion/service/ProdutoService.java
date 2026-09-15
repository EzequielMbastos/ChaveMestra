package com.ChaveMestra.Apllicattion.service;

import org.springframework.stereotype.Service;

import com.ChaveMestra.Apllicattion.model.Produto;
import com.ChaveMestra.Apllicattion.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto cadastrar(Produto produto) {
        return produtoRepository.save(produto);
    }

}
