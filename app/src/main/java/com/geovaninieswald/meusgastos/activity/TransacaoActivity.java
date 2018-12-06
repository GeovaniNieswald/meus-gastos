package com.geovaninieswald.meusgastos.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.helper.ItemOffsetDecoration;
import com.geovaninieswald.meusgastos.helper.RecyclerViewTransacaoAdapter;
import com.geovaninieswald.meusgastos.model.DAO.TransacaoDAO;

import java.text.ParseException;

public class TransacaoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView transacoes;
    private RecyclerViewTransacaoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transacao);

        toolbar = findViewById(R.id.toolbarID);
        toolbar.setTitle("Transações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configurarRecycler();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            adapter = new RecyclerViewTransacaoAdapter(TransacaoActivity.this, new TransacaoDAO(this).retornarTodas());
            transacoes.setAdapter(adapter);
        } catch (ParseException e) {
            // Tratar
        }
    }

    private void configurarRecycler() {
        transacoes = findViewById(R.id.transacoesID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        transacoes.setLayoutManager(layoutManager);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        transacoes.addItemDecoration(itemDecoration);
    }
}
