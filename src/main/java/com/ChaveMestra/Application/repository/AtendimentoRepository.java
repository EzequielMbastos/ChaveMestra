package com.ChaveMestra.Application.repository;

import com.ChaveMestra.Application.model.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Integer> {
    boolean existsByClienteId(Integer clienteId);
}
