package com.geovaninieswald.meusgastos.helper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.geovaninieswald.meusgastos.R;

public class CategoriasHolder extends RecyclerView.ViewHolder {

    public TextView descricao;
    public TextView tipo;

    public CategoriasHolder(@NonNull View itemView) {
        super(itemView);

        descricao = itemView.findViewById(R.id.descricaoID);
        tipo = itemView.findViewById(R.id.tipoID);
    }
}
