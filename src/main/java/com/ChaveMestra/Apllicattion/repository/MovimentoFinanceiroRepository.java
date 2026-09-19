package com.ChaveMestra.Apllicattion.repository;

import com.ChaveMestra.Apllicattion.model.MovimentoFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentoFinanceiroRepository extends JpaRepository<MovimentoFinanceiro, Integer> {
    boolean existsByCategoriaFinanceiraId(Integer categoriaFinanceiraId);
}
