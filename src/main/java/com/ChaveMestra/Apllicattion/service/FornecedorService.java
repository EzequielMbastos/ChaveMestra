package com.ChaveMestra.Apllicattion.service;

import com.ChaveMestra.Apllicattion.dto.FornecedorRequest;
import com.ChaveMestra.Apllicattion.dto.FornecedorResponse;
import com.ChaveMestra.Apllicattion.mapper.FornecedorMapper;
import com.ChaveMestra.Apllicattion.model.Fornecedor;
import com.ChaveMestra.Apllicattion.repository.ProdutoRepository;
import com.ChaveMestra.Apllicattion.repository.FornecedorRepository;
import org.springframework.stereotype.Service;

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

    public List<FornecedorResponse> listar() {
        List<FornecedorResponse> listFornecedorResponse = fornecedorRepository.findAll().stream().map(fornecedor -> fornecedorMapper.toResponse(fornecedor)).collect(Collectors.toList());
        return listFornecedorResponse;
    }

    public FornecedorResponse cadastrar(FornecedorRequest request) {
        Fornecedor fornecedor = fornecedorMapper.toFornecedor(request);
        Fornecedor fornecedorSalvo = fornecedorRepository.save(fornecedor);
        return fornecedorMapper.toResponse(fornecedorSalvo);
    }

    public void excluir(Integer id) {
        if (produtoRepository.existsByFornecedorId(id)) {
            throw new RuntimeException("Nao foi possivel excluir");
        }
        fornecedorRepository.deleteById(id);
    }

    public FornecedorResponse buscarPorId(Integer id) {
        Optional<Fornecedor> objetoBuscado = fornecedorRepository.findById(id);
        Fornecedor fornecedor = objetoBuscado.orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        return fornecedorMapper.toResponse(fornecedor);
    }

    public FornecedorResponse atualizar(Integer id, FornecedorRequest dados) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor nao existente"));
        fornecedor.setNome(dados.nome());
        fornecedor.setCnpj(dados.cnpj());
        fornecedor.setTelefone(dados.telefone());
        fornecedor = fornecedorRepository.save(fornecedor);
        return fornecedorMapper.toResponse(fornecedor);
    }






}
