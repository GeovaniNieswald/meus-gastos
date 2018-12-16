package com.geovaninieswald.meusgastos.model;

import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return id == categoria.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
