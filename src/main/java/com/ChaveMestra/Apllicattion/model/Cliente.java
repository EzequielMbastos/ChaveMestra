package com.ChaveMestra.Apllicattion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name="cpf", nullable = false, unique = true, length = 14)
    private  String cpf;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "endereco", columnDefinition = "TEXT")
    private String endereco;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCpf(){
        return cpf;
    }

    public void setCpf(String cpf){
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone(){
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
