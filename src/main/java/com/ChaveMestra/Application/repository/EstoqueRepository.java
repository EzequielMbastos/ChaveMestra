package com.ChaveMestra.Application.repository;

import com.ChaveMestra.Application.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

    /**
     * Busca o estoque vinculado a um produto.
     * Usado pelo EstoqueService para validar duplicidade (produto é @OneToOne, unique).
     */
    Optional<Estoque> findByProdutoId(Integer produtoId);

    /**
     * Atualiza a quantidade de estoque de um produto.
     * Usado para baixa automática quando vendas de produtos são realizadas.
     */
    @Modifying
    @Query("UPDATE Estoque e SET e.quantidade = e.quantidade - :quantidade WHERE e.produto.id = :produtoId")
    void baixarEstoque(@Param("produtoId") Integer produtoId, @Param("quantidade") int quantidade);

    /**
     * Atualiza a quantidade de estoque de um produto (adiciona).
     * Usado para devolução ou correção de estoque.
     */
    @Modifying
    @Query("UPDATE Estoque e SET e.quantidade = e.quantidade + :quantidade WHERE e.produto.id = :produtoId")
    void adicionarEstoque(@Param("produtoId") Integer produtoId, @Param("quantidade") int quantidade);
}