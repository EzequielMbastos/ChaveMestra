package com.ChaveMestra.Application.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table (name = "atendimento_item")
public class AtendimentoItem {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn (name = "atendimento_id", nullable = false)
    private Atendimento atendimento;

    @ManyToOne
    @JoinColumn (name = "produto_id")
    private Produto produto;

    @ManyToOne
    @JoinColumn (name = "servico_id")
    private Servico servico;

    @Column (name = "quantidade", nullable = false)
    private int quantidade = 1;

    @Column (name = "valor_unitario",nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column (name = "valor_total",nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column (name = "tipo", nullable = false)
    private String tipo;

    @Column ( name = "desconto", precision = 10, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;
    // No banco, o desconto tem DEFAULT 0.00. No Java, se não inicializar com BigDecimal.ZERO, fica null.
    // Inicializando com ZERO, evita NullPointerException no Service.

    @Column (name = "observacao", length = 255)
    private String observacao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Atendimento getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(Atendimento atendimento) {
        this.atendimento = atendimento;
    }
}






