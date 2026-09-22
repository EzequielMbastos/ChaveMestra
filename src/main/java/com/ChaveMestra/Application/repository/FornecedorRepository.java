
package com.ChaveMestra.Application.repository;

import com.ChaveMestra.Application.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {
}