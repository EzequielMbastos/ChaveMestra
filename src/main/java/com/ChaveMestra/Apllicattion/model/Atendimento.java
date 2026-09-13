package com.ChaveMestra.Apllicattion.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table (name = "atendimento")
public class Atendimento {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn (name = "cliente_id")
    @JoinColumn (name = "cliente_id")
    private Cliente cliente;

    @Column (name = "data", nullable = false)
    private LocalDateTime data;

    @Column (name = "forma_pagamento", nullable = false, length = 20)
    private String formaPagamento;

    @Column (name = "valor_total", nullable = false, precision = 10, scale = 2)
    private  BigDecimal valorTotal;

    @Column (name = "desconto", nullable = false, precision = 10, scale = 2)
    private BigDecimal desconto;

    //essa coluna em especial pois insertable = false = O JPA não tenta inserir esse valor
    //updatable = false	O JPA não tenta atualizar esse valor
    @Column(name = "valor_liquido", insertable = false, updatable = false)
    private BigDecimal valorLiquido;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "aberto";

    @Column(name =  "observacao", length = 255)
    private String observacao;

}
