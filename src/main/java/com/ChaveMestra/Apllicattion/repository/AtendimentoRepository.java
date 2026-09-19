package com.ChaveMestra.Apllicattion.repository;

import com.ChaveMestra.Apllicattion.model.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Integer> {
    boolean existsByClienteId(Integer clienteId);
}
