package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.dto.*;
import com.ChaveMestra.Application.model.*;
import com.ChaveMestra.Application.mapper.CategoriaFinanceiraMapper;
import com.ChaveMestra.Application.repository.CategoriaFinanceiraRepository;
import com.ChaveMestra.Application.repository.MovimentoFinanceiroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaFinanceiraService {

    private final CategoriaFinanceiraRepository categoriaFinanceiraRepository;
    private final CategoriaFinanceiraMapper categoriaFinanceiraMapper;
    private final MovimentoFinanceiroRepository movimentoFinanceiroRepository;

    public CategoriaFinanceiraService(CategoriaFinanceiraRepository categoriaFinanceiraRepository,
                             CategoriaFinanceiraMapper categoriaFinanceiraMapper, MovimentoFinanceiroRepository movimentoFinanceiroRepository) {
        this.categoriaFinanceiraRepository = categoriaFinanceiraRepository;
        this.categoriaFinanceiraMapper = categoriaFinanceiraMapper;
        this.movimentoFinanceiroRepository  = movimentoFinanceiroRepository;
    }

    public List<CategoriaFinanceiraResponse> listar() {
        List<CategoriaFinanceiraResponse> listCategoriaFinanceiraResponse = categoriaFinanceiraRepository.findAll().stream().map(categoriaFinanceira -> categoriaFinanceiraMapper.toResponse(categoriaFinanceira)).collect(Collectors.toList());
        return listCategoriaFinanceiraResponse;
    }

    public CategoriaFinanceiraResponse cadastrar(CategoriaFinanceiraRequest request) {
        CategoriaFinanceira categoriaFinanceira = categoriaFinanceiraMapper.toCategoriaFinanceira(request);
        CategoriaFinanceira categoriaFinanceiraSalva = categoriaFinanceiraRepository.save(categoriaFinanceira);
        return categoriaFinanceiraMapper.toResponse(categoriaFinanceiraSalva);
    }


    public void excluir(Integer id) {
        if (movimentoFinanceiroRepository.existsByCategoriaFinanceiraId(id)) {
            throw new RuntimeException("Nao foi possivel excluir");
        }
        categoriaFinanceiraRepository.deleteById(id);
    }

    public CategoriaFinanceiraResponse buscarPorId(Integer id) {
        Optional<CategoriaFinanceira> objetoBuscado = categoriaFinanceiraRepository.findById(id);
        CategoriaFinanceira categoriaFinanceira = objetoBuscado.orElseThrow(() -> new RuntimeException("CategoriaFinanceira não encontrado"));
        return categoriaFinanceiraMapper.toResponse(categoriaFinanceira);
    }

    public CategoriaFinanceiraResponse atualizar(Integer id, CategoriaFinanceiraRequest dados) {
        CategoriaFinanceira categoriaFinanceira = categoriaFinanceiraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria Financeira nao existente"));
        categoriaFinanceira.setNome(dados.nome());
        categoriaFinanceira.setTipo(dados.tipo());
        categoriaFinanceira.setDescricao(dados.descricao());

        categoriaFinanceira = categoriaFinanceiraRepository.save(categoriaFinanceira);
        return categoriaFinanceiraMapper.toResponse(categoriaFinanceira);
    }
}
