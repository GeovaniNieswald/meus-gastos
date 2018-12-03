package com.geovaninieswald.meusgastos.enumeration;

public enum TipoCategoria {
    DESPESA(0), RENDIMENTO(1);

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
