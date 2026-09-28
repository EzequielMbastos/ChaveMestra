package com.ChaveMestra.Application.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ia_interacao")
public class IaInteracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_pergunta", nullable = false, columnDefinition = "text")
    private String usuarioPergunta;

    @Column(name = "ia_resposta", columnDefinition = "text")
    private String iaResposta;

    @Column(name = "tipo_pergunta", nullable = false, length = 255)
    private String tipoPergunta;

    @Column(name = "data_interacao")
    private LocalDateTime dataInteracao;

    @Column(name = "tempo_resposta", precision = 10, scale = 2)
    private BigDecimal tempoResposta;

    @Column(name = "relevancia", length = 255)
    private String relevancia;

    @Column(name = "observacao", length = 255)
    private String observacao;

    @Column(name = "acao_executada", length = 255)
    private String acaoExecutada;

    @Column(name = "acao_pendente", columnDefinition = "text")
    private String acaoPendente;

    @Column(name = "avaliacao")
    private Integer avaliacao;

    @Column(name = "comentario_avaliacao", length = 500)
    private String comentarioAvaliacao;

    @Column(name = "avaliado_em")
    private LocalDateTime avaliadoEm;

    @PrePersist
    public void prePersist() {
        if (dataInteracao == null) {
            dataInteracao = LocalDateTime.now();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsuarioPergunta() {
        return usuarioPergunta;
    }

    public void setUsuarioPergunta(String usuarioPergunta) {
        this.usuarioPergunta = usuarioPergunta;
    }

    public String getIaResposta() {
        return iaResposta;
    }

    public void setIaResposta(String iaResposta) {
        this.iaResposta = iaResposta;
    }

    public String getTipoPergunta() {
        return tipoPergunta;
    }

    public void setTipoPergunta(String tipoPergunta) {
        this.tipoPergunta = tipoPergunta;
    }

    public LocalDateTime getDataInteracao() {
        return dataInteracao;
    }

    public void setDataInteracao(LocalDateTime dataInteracao) {
        this.dataInteracao = dataInteracao;
    }

    public BigDecimal getTempoResposta() {
        return tempoResposta;
    }

    public void setTempoResposta(BigDecimal tempoResposta) {
        this.tempoResposta = tempoResposta;
    }

    public String getRelevancia() {
        return relevancia;
    }

    public void setRelevancia(String relevancia) {
        this.relevancia = relevancia;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getAcaoExecutada() {
        return acaoExecutada;
    }

    public void setAcaoExecutada(String acaoExecutada) {
        this.acaoExecutada = acaoExecutada;
    }

    public String getAcaoPendente() {
        return acaoPendente;
    }

    public void setAcaoPendente(String acaoPendente) {
        this.acaoPendente = acaoPendente;
    }

    public Integer getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Integer avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getComentarioAvaliacao() {
        return comentarioAvaliacao;
    }

    public void setComentarioAvaliacao(String comentarioAvaliacao) {
        this.comentarioAvaliacao = comentarioAvaliacao;
    }

    public LocalDateTime getAvaliadoEm() {
        return avaliadoEm;
    }

    public void setAvaliadoEm(LocalDateTime avaliadoEm) {
        this.avaliadoEm = avaliadoEm;
    }
}