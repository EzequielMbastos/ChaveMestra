package com.ChaveMestra.Application.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ia_interacao")
public class IaInteracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_pergunta", nullable = false)
    private String usuarioPergunta;

    @Column(name = "ia_resposta")
    private String iaResposta;
}