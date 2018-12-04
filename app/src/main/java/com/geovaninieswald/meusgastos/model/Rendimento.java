package com.geovaninieswald.meusgastos.model;

import java.math.BigDecimal;
import java.util.Date;

public class Rendimento {

    private int id;
    private String descricao;
    private Categoria categoria;
    private BigDecimal valor;
    private Date data;
    private int quantidade;

    public Rendimento() {
    }

    public Rendimento(int id, String descricao, Categoria categoria, BigDecimal valor, Date data, int quantidade) {
        this.id = id;
        this.descricao = descricao;
        this.categoria = categoria;
        this.valor = valor;
        this.data = data;
        this.quantidade = quantidade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
