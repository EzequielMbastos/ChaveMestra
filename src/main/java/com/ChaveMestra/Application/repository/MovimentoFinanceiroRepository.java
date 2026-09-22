package com.ChaveMestra.Application.repository;

import com.ChaveMestra.Application.model.MovimentoFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentoFinanceiroRepository extends JpaRepository<MovimentoFinanceiro, Integer> {
    boolean existsByCategoriaFinanceiraId(Integer categoriaFinanceiraId);
}
