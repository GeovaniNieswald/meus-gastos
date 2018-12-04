package com.geovaninieswald.meusgastos.activity;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.geovaninieswald.meusgastos.model.Categoria;
import com.geovaninieswald.meusgastos.model.DAO.CategoriaDAO;

public class AddCategoriaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText descricao;
    private RadioButton rendimento;
    private Button adicionar;
    private ProgressBar carregando;

    private ConstraintLayout containerMeio;

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
        containerMeio = findViewById(R.id.containerMeioID);

        adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descricao.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddCategoriaActivity.this, "Informe uma descrição", Toast.LENGTH_SHORT).show();
                } else {
                    iniciarCarregamento();

                    Categoria c = new Categoria();
                    c.setDescricao(descricao.getText().toString());

                    if (rendimento.isChecked()) {
                        c.setTipoCategoria(TipoCategoria.RENDIMENTO);
                    } else {
                        c.setTipoCategoria(TipoCategoria.GASTO);
                    }

                    CategoriaDAO dao = new CategoriaDAO(AddCategoriaActivity.this);
                    long retorno = dao.salvar(c);

                    if (retorno == -2) {
                        pararCarregamento();
                        Toast.makeText(AddCategoriaActivity.this, "Categoria já existe", Toast.LENGTH_SHORT).show();
                    } else if (retorno == -1) {
                        pararCarregamento();
                        Toast.makeText(AddCategoriaActivity.this, "Não foi possível adicionar", Toast.LENGTH_SHORT).show();
                    } else {
                        // Informar sucesso, resetar tela, enviar para firebase alterar sharedPreferences sobre sincronização
                    }
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void iniciarCarregamento() {
        carregando.setVisibility(View.VISIBLE);

        for (int i = 0; i < containerMeio.getChildCount(); i++) {
            View child = containerMeio.getChildAt(i);
            child.setEnabled(false);
        }
    }

    private void pararCarregamento() {
        carregando.setVisibility(View.INVISIBLE);

        for (int i = 0; i < containerMeio.getChildCount(); i++) {
            View child = containerMeio.getChildAt(i);
            child.setEnabled(true);
        }
    }
}
