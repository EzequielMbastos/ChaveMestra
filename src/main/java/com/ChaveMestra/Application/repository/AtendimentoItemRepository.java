package com.ChaveMestra.Application.repository;

import com.ChaveMestra.Application.model.AtendimentoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtendimentoItemRepository extends JpaRepository<AtendimentoItem, Integer> {
    boolean existsByServicoId(Integer servicoId);
}
