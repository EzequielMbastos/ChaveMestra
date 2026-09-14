package com.ChaveMestra.Apllicattion.model;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table (name = "movimento_financeiro")
public class MovimentoFinanceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;


    @JoinColumn(name = "atendimento_id", nullable = false)
    private Atendimento atendimento;

    @Column(name = "pessoa", length = 100)
    private String nome;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_movimento")
    private LocalDateTime dataMovimento;

    @Column(name = "vencimento")
    private LocalDateTime vencimento;

    @Column(name = "status", nullable = false, length = 15)
    private String status = "pendente";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDateTime vencimento) {
        this.vencimento = vencimento;
    }

    public LocalDateTime getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(LocalDateTime dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Atendimento getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(Atendimento atendimento) {
        this.atendimento = atendimento;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}