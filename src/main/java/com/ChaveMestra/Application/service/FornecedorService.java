package com.ChaveMestra.Application.service;

import com.ChaveMestra.Application.dto.FornecedorRequest;
import com.ChaveMestra.Application.dto.FornecedorResponse;
import com.ChaveMestra.Application.exception.BusinessException;
import com.ChaveMestra.Application.exception.ResourceNotFoundException;
import com.ChaveMestra.Application.mapper.FornecedorMapper;
import com.ChaveMestra.Application.model.Fornecedor;
import com.ChaveMestra.Application.repository.ProdutoRepository;
import com.ChaveMestra.Application.repository.FornecedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FornecedorService{

    private final FornecedorRepository fornecedorRepository;
    private final FornecedorMapper fornecedorMapper;
    private final ProdutoRepository produtoRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository,
                          FornecedorMapper fornecedorMapper,
                          ProdutoRepository produtoRepository) {
        this.fornecedorRepository = fornecedorRepository;
        this.fornecedorMapper = fornecedorMapper;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponse> listar() {
        List<FornecedorResponse> listFornecedorResponse = fornecedorRepository.findAll().stream().map(fornecedor -> fornecedorMapper.toResponse(fornecedor)).collect(Collectors.toList());
        return listFornecedorResponse;
    }

    @Transactional
    public FornecedorResponse cadastrar(FornecedorRequest request) {
        Fornecedor fornecedor = fornecedorMapper.toFornecedor(request);
        Fornecedor fornecedorSalvo = fornecedorRepository.save(fornecedor);
        return fornecedorMapper.toResponse(fornecedorSalvo);
    }

    @Transactional
    public void excluir(Integer id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fornecedor não encontrado");
        }
        if (produtoRepository.existsByFornecedorId(id)) {
            throw new BusinessException("Não foi possível excluir o fornecedor com produtos vinculados");
        }
        fornecedorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public FornecedorResponse buscarPorId(Integer id) {
        Optional<Fornecedor> objetoBuscado = fornecedorRepository.findById(id);
        Fornecedor fornecedor = objetoBuscado.orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"));
        return fornecedorMapper.toResponse(fornecedor);
    }

    @Transactional
    public FornecedorResponse atualizar(Integer id, FornecedorRequest dados) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"));
        fornecedor.setNome(dados.nome());
        fornecedor.setCnpj(dados.cnpj());
        fornecedor.setTelefone(dados.telefone());
        fornecedor = fornecedorRepository.save(fornecedor);
        return fornecedorMapper.toResponse(fornecedor);
    }






}
