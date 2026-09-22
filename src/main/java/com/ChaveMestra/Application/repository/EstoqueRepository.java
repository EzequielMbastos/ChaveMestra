package com.ChaveMestra.Application.repository;

import com.ChaveMestra.Application.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

    /**
     * Busca o estoque vinculado a um produto.
     * Usado pelo EstoqueService para validar duplicidade (produto é @OneToOne, unique).
     */
    Optional<Estoque> findByProdutoId(Integer produtoId);
}