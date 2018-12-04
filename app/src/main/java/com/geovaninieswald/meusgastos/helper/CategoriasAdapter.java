package com.geovaninieswald.meusgastos.helper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.geovaninieswald.meusgastos.model.Categoria;

import java.util.List;

public class CategoriasAdapter extends RecyclerView.Adapter<CategoriasHolder> {

    private final List<Categoria> CATEGORIAS;

    public CategoriasAdapter(List<Categoria> categorias) {
        this.CATEGORIAS = categorias;
    }

    @NonNull
    @Override
    public CategoriasHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CategoriasHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.linha_categoria, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriasHolder categoriasHolder, int i) {
        categoriasHolder.descricao.setText(CATEGORIAS.get(i).getDescricao());

        if (CATEGORIAS.get(i).getTipoCategoria() == TipoCategoria.GASTO) {
            categoriasHolder.tipo.setText("Gasto");
        } else {
            categoriasHolder.tipo.setText("Rendimento");
        }
    }

    @Override
    public int getItemCount() {
        return CATEGORIAS != null ? CATEGORIAS.size() : 0;
    }
}
