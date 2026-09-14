package com.ChaveMestra.Apllicattion.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table (name = "estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "produto_id", nullable = false, unique = true)
    private Produto produto;

    @Column(name = "quantidade", nullable = false)
    private int quantidade = 0;

    @Column(name = "minimo", nullable = false)
    private int minimo = 5;

    @Column(name = "dtEntrada")
    private LocalDateTime dtEntrada;

    @Column(name = "dtAtualizacao")
    private LocalDateTime dtAtualizacao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getMinimo() {
        return minimo;
    }

    public void setMinimo(int minimo) {
        this.minimo = minimo;
    }

    public LocalDateTime getDtEntrada() {
        return dtEntrada;
    }

    public void setDtEntrada(LocalDateTime dtEntrada) {
        this.dtEntrada = dtEntrada;
    }

    public LocalDateTime getDtAtualizacao() {
        return dtAtualizacao;
    }

    public void setDtAtualizacao(LocalDateTime dtAtualizacao) {
        this.dtAtualizacao = dtAtualizacao;
    }
}