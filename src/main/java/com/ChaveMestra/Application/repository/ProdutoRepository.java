package com.ChaveMestra.Application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ChaveMestra.Application.model.Produto;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    List<Produto> findByAtivoTrue();

    Optional<Produto> findByIdAndAtivoTrue(Integer id);

    boolean existsByCategoriaId(Integer categoriaId);

    boolean existsByFornecedorId(Integer fornecedorId);

    //O JpaRepository já nos entrega operações básicas como:
//
//save()
//findById()
//findAll()
//deleteById()
//existsById()
}
