package com.geovaninieswald.meusgastos.model;

import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Categoria implements Serializable {

    private int id;
    private TipoCategoria tipoCategoria;
    private String descricao;

    public Categoria() {
    }

    public Categoria(int id, TipoCategoria tipoCategoria, String descricao) {
        this.id = id;
        this.tipoCategoria = tipoCategoria;
        this.descricao = descricao;
    }

    @Exclude
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TipoCategoria getTipoCategoria() {
        return tipoCategoria;
    }

    public void setTipoCategoria(TipoCategoria tipoCategoria) {
        this.tipoCategoria = tipoCategoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
