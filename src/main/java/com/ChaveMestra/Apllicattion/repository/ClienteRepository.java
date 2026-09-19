package com.ChaveMestra.Apllicattion.repository;

import com.ChaveMestra.Apllicattion.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}
