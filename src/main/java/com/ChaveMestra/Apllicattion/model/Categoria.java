package com.ChaveMestra.Apllicattion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "descricao", length = 100)
    private String descricao;

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
