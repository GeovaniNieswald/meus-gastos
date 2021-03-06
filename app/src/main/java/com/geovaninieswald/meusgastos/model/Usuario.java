package com.geovaninieswald.meusgastos.model;

public class Usuario {

    private String id;
    private String nome;
    private String imagem;
    private String email;

    public Usuario() {
    }

    public Usuario(String id, String nome, String imagem, String email) {
        this.id = id;
        this.nome = nome;
        this.imagem = imagem;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
