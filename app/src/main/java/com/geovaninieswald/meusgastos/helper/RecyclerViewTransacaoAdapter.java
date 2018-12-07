package com.geovaninieswald.meusgastos.helper;

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
import com.geovaninieswald.meusgastos.activity.AddTransacaoActivity;
import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.geovaninieswald.meusgastos.model.Transacao;

import java.util.List;

public class RecyclerViewTransacaoAdapter extends RecyclerView.Adapter<RecyclerViewTransacaoAdapter.TransacaoViewHolder> {

    private List<Transacao> transacoes;
    private Context context;

    public RecyclerViewTransacaoAdapter(Context context, List<Transacao> transacoes) {
        this.transacoes = transacoes;
        this.context = context;
    }

    @NonNull
    @Override
    public TransacaoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TransacaoViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.linha_transacao, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransacaoViewHolder transacaoViewHolder, int i) {
        final int POSICAO = i;

        transacaoViewHolder.descricao.setText(transacoes.get(i).getDescricao());
        transacaoViewHolder.categoria.setText(transacoes.get(i).getCategoria().getDescricao());
        transacaoViewHolder.valor.setText("R$" + Utils.prepararValor(transacoes.get(i).getValor()));
        transacaoViewHolder.data.setText(Utils.dateParaString(transacoes.get(i).getData()));

        if (transacoes.get(i).getCategoria().getTipoCategoria() == TipoCategoria.RENDIMENTO) {
            transacaoViewHolder.tipoTransacao.setText("Rendimento");
            transacaoViewHolder.linha.setBackground(context.getDrawable(R.drawable.border_rendimento));
        } else {
            transacaoViewHolder.tipoTransacao.setText("Gasto");
            transacaoViewHolder.linha.setBackground(context.getDrawable(R.drawable.border_gasto));
        }

        if (transacoes.get(i).isPago())
            transacaoViewHolder.containerPago.setVisibility(View.VISIBLE);

        transacaoViewHolder.linha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddTransacaoActivity.class);
                intent.putExtra("transacao", transacoes.get(POSICAO));
                intent.putExtra("alterar", true);

                if (transacoes.get(POSICAO).getCategoria().getTipoCategoria() == TipoCategoria.RENDIMENTO) {
                    context.startActivity(intent.putExtra("gasto", false));
                } else {
                    context.startActivity(intent.putExtra("gasto", true));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return transacoes != null ? transacoes.size() : 0;
    }

    public void removerTransacao(int posicao) {
        transacoes.remove(posicao);
        notifyItemRemoved(posicao);
        notifyItemRangeChanged(posicao, getItemCount());
    }

    public void cancelarRemocao(int posicao) {
        notifyItemChanged(posicao);
    }

    public long getDbId(int posicao) {
        return transacoes.get(posicao).getId();
    }

    public class TransacaoViewHolder extends RecyclerView.ViewHolder {

        public TextView descricao, categoria, tipoTransacao, valor, data;
        public ConstraintLayout linha;
        public ConstraintLayout containerPago;

        public TransacaoViewHolder(@NonNull View itemView) {
            super(itemView);

            descricao = itemView.findViewById(R.id.descricaoID);
            categoria = itemView.findViewById(R.id.categoriaID);
            tipoTransacao = itemView.findViewById(R.id.tipoTransacaoID);
            valor = itemView.findViewById(R.id.valorID);
            data = itemView.findViewById(R.id.dataID);
            linha = itemView.findViewById(R.id.linhaID);
            containerPago = itemView.findViewById(R.id.containerPagoID);
        }
    }
}
