package com.ChaveMestra.Application.service;
import com.ChaveMestra.Application.dto.CategoriaRequest;
import com.ChaveMestra.Application.dto.CategoriaResponse;
import com.ChaveMestra.Application.mapper.CategoriaMapper;
import com.ChaveMestra.Application.model.Categoria;
import com.ChaveMestra.Application.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
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

    public CategoriaResponse cadastrar(CategoriaRequest request)
    {
        Categoria categoria = categoriaMapper.toCategoria(request);
        Categoria categoriaSalva = categoriaRepository.save(categoria);
        return categoriaMapper.toResponse(categoriaSalva);
    }

    public List<CategoriaResponse> listar(){
        List<CategoriaResponse> listCategoriaResponse =  categoriaRepository.findAll().stream()
                .map(categoria -> categoriaMapper.toResponse(categoria))
                .collect(Collectors.toList());
        return listCategoriaResponse;
    }

    public void excluir(Integer id) {
        if (produtoRepository.existsByCategoriaId(id)) {
            throw new RuntimeException("Nao foi possivel excluir");
        }
        categoriaRepository.deleteById(id);
    }

    public CategoriaResponse buscarPorId(Integer id) {
        Optional<Categoria> objetoBuscado = categoriaRepository.findById(id);
        Categoria categoria = objetoBuscado.orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        return categoriaMapper.toResponse(categoria);
    }


        public CategoriaResponse atualizar(Integer id, CategoriaRequest dados) {
            Categoria categoria = categoriaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Categoria nao existente"));
            categoria.setNome(dados.nome());
            categoria.setDescricao(dados.descricao());
            categoria = categoriaRepository.save(categoria);
            return categoriaMapper.toResponse(categoria);
        }
    }




