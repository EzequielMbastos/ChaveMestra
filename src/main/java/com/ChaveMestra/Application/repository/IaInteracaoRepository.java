package com.ChaveMestra.Application.repository;

import com.ChaveMestra.Application.model.IaInteracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IaInteracaoRepository extends JpaRepository<IaInteracao, Integer> {
    List<IaInteracao> findAllByOrderByDataInteracaoDesc();

    List<IaInteracao> findByTipoPergunta(String tipoPergunta);
}
