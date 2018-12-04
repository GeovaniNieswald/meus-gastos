package com.geovaninieswald.meusgastos.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.geovaninieswald.meusgastos.model.Categoria;

public class AddCategoriaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText descricao;
    private RadioButton rendimento;
    private Button adicionar;
    private ProgressBar carregando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_categoria);

        toolbar = findViewById(R.id.toolbarID);
        toolbar.setTitle("Adicionar Categoria");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        descricao = findViewById(R.id.descricaoID);
        rendimento = findViewById(R.id.rendimentoID);
        adicionar = findViewById(R.id.adicionarID);
        carregando = findViewById(R.id.carregandoID);

        adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descricao.getText().toString().trim().isEmpty()) {
                    // Mensagem de erro
                } else {
                    Categoria c = new Categoria();
                    c.setDescricao(descricao.getText().toString());

                    if (rendimento.isChecked()) {
                        c.setTipoCategoria(TipoCategoria.RENDIMENTO);
                    } else {
                        c.setTipoCategoria(TipoCategoria.GASTO);
                    }

                    // Salvar nova categoria!
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
