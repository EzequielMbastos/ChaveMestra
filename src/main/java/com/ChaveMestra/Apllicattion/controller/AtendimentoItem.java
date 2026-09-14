package com.ChaveMestra.Apllicattion.controller;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table (name = "atendimento_item")
public class AtendimentoItem {
    @Id 
    @GeneratedValue(strategy =  GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private  Double preco;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private  Double subtotal;
    
    public AtendimentoItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }



    
}
