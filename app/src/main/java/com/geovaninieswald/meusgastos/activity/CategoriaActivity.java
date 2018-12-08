package com.geovaninieswald.meusgastos.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.geovaninieswald.meusgastos.helper.ItemOffsetDecoration;
import com.geovaninieswald.meusgastos.helper.RecyclerViewCategoriaAdapter;
import com.geovaninieswald.meusgastos.helper.Utils;
import com.geovaninieswald.meusgastos.model.DAO.CategoriaDAO;

public class CategoriaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fabAdd;
    private RecyclerView categorias;
    private RecyclerViewCategoriaAdapter adapter;

    private boolean transacao;
    private boolean gasto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        toolbar = findViewById(R.id.toolbarID);
        fabAdd = findViewById(R.id.fabAddID);
        categorias = findViewById(R.id.categoriasID);

        toolbar.setTitle("Categorias");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoriaActivity.this, AddCategoriaActivity.class));
            }
        });

        transacao = getIntent().getBooleanExtra("transacao", false);

        if (transacao) {
            gasto = getIntent().getBooleanExtra("gasto", false);
        }

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

        if (transacao) {
            TipoCategoria tc = TipoCategoria.GASTO;

            if (!gasto)
                tc = TipoCategoria.RENDIMENTO;

            adapter = new RecyclerViewCategoriaAdapter(CategoriaActivity.this, new CategoriaDAO(this).retornarPorTipo(tc));
        } else {
            adapter = new RecyclerViewCategoriaAdapter(CategoriaActivity.this, new CategoriaDAO(this).retornarTodas());
        }

        categorias.setAdapter(adapter);
    }

    private void configurarRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        categorias.setLayoutManager(layoutManager);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        categorias.addItemDecoration(itemDecoration);

        categorias.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && fabAdd.getVisibility() == View.VISIBLE) {
                    fabAdd.hide();
                } else if (dy < 0 && fabAdd.getVisibility() != View.VISIBLE) {
                    fabAdd.show();
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int POSICAO = viewHolder.getAdapterPosition();

                AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.itemView.getContext());

                final AlertDialog alert = builder.setTitle("Confirmação")
                        .setMessage("Tem certeza que deseja excluir esta categoria?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CategoriaDAO dao = new CategoriaDAO(getBaseContext());
                                int numItens = dao.excluir(adapter.getDbId(POSICAO));

                                if (numItens > 0) {
                                    adapter.removerCategoria(POSICAO);
                                    // Excluir do firebase
                                    Utils.mostrarMensagemCurta(CategoriaActivity.this, "Categoria Excluida");
                                } else {
                                    Utils.mostrarMensagemCurta(CategoriaActivity.this, "Não foi possível excluir a categoria");
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.cancelarRemocao(POSICAO);
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                adapter.cancelarRemocao(POSICAO);
                            }
                        })
                        .create();

                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                });

                alert.show();
            }
        });

        itemTouchHelper.attachToRecyclerView(categorias);
    }
}
