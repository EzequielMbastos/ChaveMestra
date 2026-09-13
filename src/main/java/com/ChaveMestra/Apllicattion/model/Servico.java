package com.ChaveMestra.Apllicattion.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table (name = "servico")
public class Servico {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @Column (name = "nome", nullable = false, length = 100)
    private String nome;

    @Column (name = "descricao" , length = 255)
    private String descricao;

    @Column (name = "preco_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoBase = BigDecimal.ZERO;
}
