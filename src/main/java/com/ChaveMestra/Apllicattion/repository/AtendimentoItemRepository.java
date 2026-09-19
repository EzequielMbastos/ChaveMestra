package com.ChaveMestra.Apllicattion.repository;

import com.ChaveMestra.Apllicattion.model.AtendimentoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtendimentoItemRepository extends JpaRepository<AtendimentoItem, Integer> {
    boolean existsByServicoId(Integer servicoId);
}
