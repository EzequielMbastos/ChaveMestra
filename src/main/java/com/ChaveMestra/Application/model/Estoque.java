package com.ChaveMestra.Application.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table (name = "estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "produto_id", nullable = false, unique = true)
    private Produto produto;

    @Column(name = "quantidade", nullable = false)
    private int quantidade = 0;

    @Column(name = "minimo", nullable = false)
    private int minimo = 5;

    @Column(name = "dt_entrada")
    private LocalDateTime dtEntrada;

    @Column(name = "dt_atualizacao")
    private LocalDateTime dtAtualizacao;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estoque estoque = (Estoque) o;
        return id != null && id.equals(estoque.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        if (dtEntrada == null) dtEntrada = agora;
        if (dtAtualizacao == null) dtAtualizacao = agora;
    }

    @PreUpdate
    public void preUpdate() {
        dtAtualizacao = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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