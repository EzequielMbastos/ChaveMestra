package com.ChaveMestra.Application.repository;

import com.ChaveMestra.Application.model.IaInteracao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IaInteracaoRepository extends JpaRepository<IaInteracao, Integer> {
    List<IaInteracao> findAllByOrderByDataInteracaoDesc();

    List<IaInteracao> findByTipoPergunta(String tipoPergunta);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select interacao from IaInteracao interacao where interacao.id = :id")
    Optional<IaInteracao> findByIdForUpdate(@Param("id") Integer id);
}
