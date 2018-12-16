package com.geovaninieswald.meusgastos.model;

import com.geovaninieswald.meusgastos.helper.Utils;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

public class Transacao implements Serializable {

    private int id;
    private String descricao;
    private Categoria categoria;
    private BigDecimal valorBD;
    private String valor;
    private Date dataBD;
    private String data;
    private boolean pago;
    private int quantidade;

    public Transacao() {
    }

    public Transacao(int id, String descricao, Categoria categoria, BigDecimal valorBD, Date dataBD, boolean pago, int quantidade) {
        this.id = id;
        this.descricao = descricao;
        this.categoria = categoria;
        this.valorBD = valorBD;
        this.dataBD = dataBD;
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

    @Exclude
    public BigDecimal getValorBD() {
        return valorBD;
    }

    @Exclude
    public void setValorBD(BigDecimal valorBD) {
        this.valorBD = valorBD;
    }

    public String getValor() {
        return valorBD.toString();
    }

    public void setValor(String valor) {
        this.valorBD = new BigDecimal(valor);
    }

    @Exclude
    public Date getDataBD() {
        return dataBD;
    }

    @Exclude
    public void setDataBD(Date dataBD) {
        this.dataBD = dataBD;
    }

    public String getData() {
        return Utils.dateParaString(dataBD);
    }

    public void setData(String data) {
        try {
            this.dataBD = Utils.stringParaDate(data);
        } catch (ParseException e) {
            // tratar
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transacao transacao = (Transacao) o;
        return id == transacao.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
