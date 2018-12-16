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

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.geovaninieswald.meusgastos.helper.SharedFirebasePreferences;
import com.geovaninieswald.meusgastos.helper.Utils;
import com.geovaninieswald.meusgastos.model.Categoria;
import com.geovaninieswald.meusgastos.model.DAO.CategoriaDAO;

public class AddCategoriaActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText descricao;
    private RadioButton rendimento;
    private Button adicionar;
    private ProgressBar carregando;
    private ConstraintLayout containerMeio;

    private SharedFirebasePreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_categoria);

        descricao = findViewById(R.id.descricaoID);
        rendimento = findViewById(R.id.rendimentoID);
        carregando = findViewById(R.id.carregandoID);
        containerMeio = findViewById(R.id.containerMeioID);
        toolbar = findViewById(R.id.toolbarID);
        adicionar = findViewById(R.id.adicionarID);

        toolbar.setTitle("Adicionar Categoria");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        adicionar.setOnClickListener(this);

        preferencias = new SharedFirebasePreferences(AddCategoriaActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.adicionarID:
                if (descricao.getText().toString().trim().isEmpty()) {
                    Utils.mostrarMensagemCurta(AddCategoriaActivity.this, "Informe uma descrição");
                } else {
                    Utils.iniciarCarregamento(carregando, containerMeio);

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
                        Utils.pararCarregamento(carregando, containerMeio);
                        Utils.mostrarMensagemCurta(AddCategoriaActivity.this, "Categoria já existe");
                    } else if (retorno == -1) {
                        Utils.pararCarregamento(carregando, containerMeio);
                        Utils.mostrarMensagemCurta(AddCategoriaActivity.this, "Não foi possível adicionar");
                    } else {
                        Utils.pararCarregamento(carregando, containerMeio);

                        descricao.setText("");
                        rendimento.setChecked(true);

                        Utils.mostrarMensagemCurta(AddCategoriaActivity.this, "Categoria adicionada com sucesso");
                        preferencias.salvarStatusSincronia(false);
                    }
                }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
