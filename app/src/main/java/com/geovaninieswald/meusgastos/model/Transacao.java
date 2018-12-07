package com.geovaninieswald.meusgastos.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Transacao implements Serializable {

    private int id;
    private String descricao;
    private Categoria categoria;
    private BigDecimal valor;
    private Date data;
    private boolean pago;
    private int quantidade;

    public Transacao() {
    }

    public Transacao(int id, String descricao, Categoria categoria, BigDecimal valor, Date data, boolean pago, int quantidade) {
        this.id = id;
        this.descricao = descricao;
        this.categoria = categoria;
        this.valor = valor;
        this.data = data;
        this.pago = pago;
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

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
