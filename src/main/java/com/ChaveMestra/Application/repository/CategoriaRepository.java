    package com.ChaveMestra.Application.repository;
    import com.ChaveMestra.Application.model.Categoria;
    import org.springframework.data.jpa.repository.JpaRepository;

    public interface CategoriaRepository  extends JpaRepository<Categoria, Integer> {



    }