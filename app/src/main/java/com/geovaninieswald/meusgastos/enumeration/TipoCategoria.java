package com.geovaninieswald.meusgastos.enumeration;

import java.io.Serializable;

public enum TipoCategoria implements Serializable {
    GASTO(0), RENDIMENTO(1);

    private int codigo;

    TipoCategoria(int codigo) {
        this.codigo = codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}
