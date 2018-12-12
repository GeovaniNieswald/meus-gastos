package com.geovaninieswald.meusgastos.helper;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.geovaninieswald.meusgastos.R;

import java.math.BigDecimal;
import java.util.List;

public class RecyclerViewRelatorioAdapter extends RecyclerView.Adapter<RecyclerViewRelatorioAdapter.RelatorioViewHolder> {

    private List<ContentValues> contentValuesList;
    private Context context;

    public RecyclerViewRelatorioAdapter(Context context, List<ContentValues> contentValuesList) {
        this.contentValuesList = contentValuesList;
        this.context = context;
    }

    @NonNull
    @Override
    public RelatorioViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RelatorioViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.linha_relatorio, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RelatorioViewHolder relatorioViewHolder, int i) {
        relatorioViewHolder.cor.setBackground(new ColorDrawable(contentValuesList.get(i).getAsInteger("cor")));
        relatorioViewHolder.descricao.setText(contentValuesList.get(i).getAsString("descricao"));
        relatorioViewHolder.valor.setText("R$" + Utils.prepararValor(BigDecimal.valueOf(contentValuesList.get(i).getAsDouble("valor"))));
        relatorioViewHolder.porcentagem.setText(contentValuesList.get(i).getAsDouble("porcentagem") + "%");
    }

    @Override
    public int getItemCount() {
        return contentValuesList != null ? contentValuesList.size() : 0;
    }

    protected class RelatorioViewHolder extends RecyclerView.ViewHolder {

        protected Button cor;
        protected TextView descricao, valor, porcentagem;
        protected ConstraintLayout linha;

        protected RelatorioViewHolder(@NonNull View itemView) {
            super(itemView);

            cor = itemView.findViewById(R.id.corID);
            descricao = itemView.findViewById(R.id.descricaoID);
            valor = itemView.findViewById(R.id.valorID);
            porcentagem = itemView.findViewById(R.id.porcentagemID);
            linha = itemView.findViewById(R.id.linhaID);
        }
    }
}