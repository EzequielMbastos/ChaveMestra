package com.ChaveMestra.Application.repository;

import com.ChaveMestra.Application.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}
