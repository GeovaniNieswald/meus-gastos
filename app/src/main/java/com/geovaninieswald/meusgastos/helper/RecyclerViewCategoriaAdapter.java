package com.geovaninieswald.meusgastos.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.geovaninieswald.meusgastos.model.Categoria;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewCategoriaAdapter extends RecyclerView.Adapter<RecyclerViewCategoriaAdapter.CategoriaViewHolder> {

    private List<Categoria> categorias;
    private Context context;

    public RecyclerViewCategoriaAdapter(Context context, List<Categoria> categorias) {
        this.categorias = categorias;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CategoriaViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.linha_categoria, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder categoriaViewHolder, int i) {
        final int posicao = i;

        categoriaViewHolder.descricao.setText(categorias.get(i).getDescricao());

        if (categorias.get(i).getTipoCategoria() == TipoCategoria.GASTO) {
            categoriaViewHolder.tipo.setText("Gasto");
        } else {
            categoriaViewHolder.tipo.setText("Rendimento");
        }

        categoriaViewHolder.linha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Activity) context).getIntent().getBooleanExtra("transacao", false)) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("categoria", categorias.get(posicao));
                    ((Activity) context).setResult(Activity.RESULT_OK, resultIntent);
                    ((Activity) context).finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorias != null ? categorias.size() : 0;
    }

    public void atualizarLista(List<Categoria> categorias) {
        this.categorias = new ArrayList<>();
        this.categorias.addAll(categorias);

        notifyDataSetChanged();
    }

    public void removerCategoria(int posicao) {
        categorias.remove(posicao);
        notifyItemRemoved(posicao);
        notifyItemRangeChanged(posicao, getItemCount());
    }

    public void cancelarRemocao(int posicao) {
        notifyItemChanged(posicao);
    }

    public long getDbId(int posicao) {
        return categorias.get(posicao).getId();
    }

    protected class CategoriaViewHolder extends RecyclerView.ViewHolder {

        protected TextView descricao;
        protected TextView tipo;
        protected ConstraintLayout linha;

        protected CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);

            descricao = itemView.findViewById(R.id.descricaoID);
            tipo = itemView.findViewById(R.id.tipoID);
            linha = itemView.findViewById(R.id.linhaID);
        }
    }
}
