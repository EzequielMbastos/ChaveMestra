package com.ChaveMestra.Application.service;
import com.ChaveMestra.Application.dto.CategoriaRequest;
import com.ChaveMestra.Application.dto.CategoriaResponse;
import com.ChaveMestra.Application.exception.BusinessException;
import com.ChaveMestra.Application.exception.ResourceNotFoundException;
import com.ChaveMestra.Application.mapper.CategoriaMapper;
import com.ChaveMestra.Application.model.Categoria;
import com.ChaveMestra.Application.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ChaveMestra.Application.repository.CategoriaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    private final ProdutoRepository produtoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository,
                            CategoriaMapper categoriaMapper, ProdutoRepository produtoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public CategoriaResponse cadastrar(CategoriaRequest request)
    {
        Categoria categoria = categoriaMapper.toCategoria(request);
        Categoria categoriaSalva = categoriaRepository.save(categoria);
        return categoriaMapper.toResponse(categoriaSalva);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar(){
        List<CategoriaResponse> listCategoriaResponse =  categoriaRepository.findAll().stream()
                .map(categoria -> categoriaMapper.toResponse(categoria))
                .collect(Collectors.toList());
        return listCategoriaResponse;
    }

    @Transactional
    public void excluir(Integer id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada");
        }
        if (produtoRepository.existsByCategoriaId(id)) {
            throw new BusinessException("Não foi possível excluir a categoria com produtos vinculados");
        }
        categoriaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Integer id) {
        Optional<Categoria> objetoBuscado = categoriaRepository.findById(id);
        Categoria categoria = objetoBuscado.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
        return categoriaMapper.toResponse(categoria);
    }


        @Transactional
        public CategoriaResponse atualizar(Integer id, CategoriaRequest dados) {
            Categoria categoria = categoriaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            categoria.setNome(dados.nome());
            categoria.setDescricao(dados.descricao());
            categoria = categoriaRepository.save(categoria);
            return categoriaMapper.toResponse(categoria);
        }
    }


