package com.ChaveMestra.Application.repository;

import com.ChaveMestra.Application.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepository extends JpaRepository<Servico, Integer> {
}