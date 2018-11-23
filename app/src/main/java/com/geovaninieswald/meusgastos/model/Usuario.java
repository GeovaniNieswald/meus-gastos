package com.geovaninieswald.meusgastos.model;

import com.geovaninieswald.meusgastos.model.DAO.ConexaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Usuario {

    private String id;
    private String nome;
    private String imagem;
    private String email;
    private String senha;

    public Usuario() {
    }

    public Usuario(String id, String nome, String imagem, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.imagem = imagem;
        this.email = email;
        this.senha = senha;
    }

    public void salvar() {
        DatabaseReference referencia = ConexaoFirebase.getFirebase();
        referencia.child("usuario").child(String.valueOf(getId())).setValue(this);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> hashMapUsuario = new HashMap<>();

        hashMapUsuario.put("id", getId());
        hashMapUsuario.put("nome", getNome());
        hashMapUsuario.put("imagem", getImagem());
        hashMapUsuario.put("email", getEmail());
        hashMapUsuario.put("senha", getSenha());

        return hashMapUsuario;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
