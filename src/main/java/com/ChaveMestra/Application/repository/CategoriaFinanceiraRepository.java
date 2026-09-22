    package com.ChaveMestra.Application.repository;

    import com.ChaveMestra.Application.model.CategoriaFinanceira;
    import org.springframework.data.jpa.repository.JpaRepository;

    public interface CategoriaFinanceiraRepository extends JpaRepository<CategoriaFinanceira, Integer> {
    }