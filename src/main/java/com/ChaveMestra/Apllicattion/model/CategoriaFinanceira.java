package com.ChaveMestra.Apllicattion.model;
import jakarta.persistence.*;

@Entity
@Table (name = "categoria_financeira")
public class CategoriaFinanceira {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name = "nome", nullable = false, length = 50)
    private String nome;

    @Column (name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column (name = "descricao", length = 100)
    private String descricao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
