package com.ChaveMestra.Apllicattion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ChaveMestra.Apllicattion.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
//O JpaRepository já nos entrega operações básicas como:
//
//save()
//findById()
//findAll()
//deleteById()
//existsById()
}
