package com.geovaninieswald.meusgastos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.helper.CategoriasAdapter;
import com.geovaninieswald.meusgastos.model.DAO.CategoriaDAO;

public class CategoriaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fabAdd;

    private RecyclerView categorias;
    private CategoriasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        toolbar = findViewById(R.id.toolbarID);
        toolbar.setTitle("Categorias");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fabAdd = findViewById(R.id.fabAddID);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoriaActivity.this, AddCategoriaActivity.class));
            }
        });

        configurarRecycler();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void configurarRecycler() {
        categorias = findViewById(R.id.categoriasID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        categorias.setLayoutManager(layoutManager);

        CategoriaDAO dao = new CategoriaDAO(this);
        adapter = new CategoriasAdapter(dao.retornarTodas());
        categorias.setAdapter(adapter);
        categorias.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
}
