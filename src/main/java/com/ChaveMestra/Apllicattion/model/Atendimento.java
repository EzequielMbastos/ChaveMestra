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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}

