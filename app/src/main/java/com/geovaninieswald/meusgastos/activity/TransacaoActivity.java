package com.geovaninieswald.meusgastos.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.geovaninieswald.meusgastos.helper.ItemOffsetDecoration;
import com.geovaninieswald.meusgastos.helper.RecyclerViewTransacaoAdapter;
import com.geovaninieswald.meusgastos.helper.SharedFirebasePreferences;
import com.geovaninieswald.meusgastos.helper.Utils;
import com.geovaninieswald.meusgastos.model.DAO.TransacaoDAO;
import com.geovaninieswald.meusgastos.model.Transacao;
import com.github.clans.fab.FloatingActionMenu;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class TransacaoActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    private Toolbar toolbar;
    private FloatingActionMenu famMenu;
    private com.github.clans.fab.FloatingActionButton fabReceita, fabGasto;
    private TextView mesAno, total;
    private Button mesAnterior, proximoMes;

    private RecyclerView transacoes;
    private RecyclerViewTransacaoAdapter adapter;

    private GregorianCalendar cal;
    private Date mesAnoDate;
    private Locale local;

    private List<Transacao> transacoesList;

    private SharedFirebasePreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transacao);

        local = new Locale("pt", "BR");
        cal = new GregorianCalendar();

        transacoes = findViewById(R.id.transacoesID);
        toolbar = findViewById(R.id.toolbarID);
        famMenu = findViewById(R.id.famMenuID);
        fabReceita = findViewById(R.id.fabRendimentoID);
        fabGasto = findViewById(R.id.fabGastoID);
        total = findViewById(R.id.totalID);
        mesAnterior = findViewById(R.id.mesAnteriorID);
        mesAno = findViewById(R.id.mesAnoID);
        proximoMes = findViewById(R.id.proximoMesID);

        toolbar.setTitle("Transações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mesAnterior.setOnClickListener(this);
        mesAnterior.setOnTouchListener(onTouchListener);
        proximoMes.setOnClickListener(this);
        proximoMes.setOnTouchListener(onTouchListener);

        fabGasto.setOnClickListener(this);
        fabReceita.setOnClickListener(this);

        configurarRecycler();

        preferencias = new SharedFirebasePreferences(TransacaoActivity.this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mesAnoDate = new Date();
        cal.setTime(mesAnoDate);

        setAdapter("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mesAnteriorID:
                if (cal.get(Calendar.MONTH) == Calendar.JANUARY) {
                    cal.roll(Calendar.MONTH, false);
                    cal.roll(Calendar.YEAR, false);
                } else {
                    cal.roll(Calendar.MONTH, false);
                }

                mesAnoDate = cal.getTime();

                setAdapter("");
                break;
            case R.id.proximoMesID:
                if (cal.get(Calendar.MONTH) == Calendar.DECEMBER) {
                    cal.roll(Calendar.MONTH, true);
                    cal.roll(Calendar.YEAR, true);
                } else {
                    cal.roll(Calendar.MONTH, true);
                }

                mesAnoDate = cal.getTime();

                setAdapter("");
                break;
            case R.id.fabGastoID:
                famMenu.close(true);
                startActivity(new Intent(TransacaoActivity.this, AddTransacaoActivity.class).putExtra("gasto", true));
                break;
            case R.id.fabRendimentoID:
                famMenu.close(true);
                startActivity(new Intent(TransacaoActivity.this, AddTransacaoActivity.class).putExtra("gasto", false));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pesquisa_menu, menu);

        MenuItem pesquisa = menu.findItem(R.id.pesquisaID);
        SearchView searchView = (SearchView) pesquisa.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        String descricao = s.toLowerCase();
        setAdapter(descricao);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        String descricao = s.toLowerCase();
        setAdapter(descricao);
        return true;
    }

    private void setAdapter(String descricao) {
        mesAno.setText(Utils.primeriaLetraMaiuscula(new SimpleDateFormat("MMMM 'de' yyyy", local).format(mesAnoDate)));

        if (descricao.trim().isEmpty()) {
            try {
                transacoesList = new TransacaoDAO(this).retornarPorMesAno(mesAnoDate);

                atualizarTotal(transacoesList);

                adapter = new RecyclerViewTransacaoAdapter(TransacaoActivity.this, transacoesList);
                transacoes.setAdapter(adapter);
            } catch (ParseException e) {
                // Tratar
            }
        } else {
            List<Transacao> newList = new ArrayList<>();

            for (Transacao t : transacoesList) {
                if (t.getDescricao().toLowerCase().contains(descricao)) {
                    newList.add(t);
                }
            }

            atualizarTotal(newList);
            adapter.atualizarLista(newList);
        }
    }

    private void atualizarTotal(List<Transacao> newList) {
        BigDecimal totalBD = BigDecimal.valueOf(0.0);

        for (Transacao t : newList) {
            if (t.isPago()) {
                if (t.getCategoria().getTipoCategoria() == TipoCategoria.GASTO) {
                    totalBD = totalBD.subtract(t.getValorBD());
                } else if (t.getCategoria().getTipoCategoria() == TipoCategoria.RENDIMENTO) {
                    totalBD = totalBD.add(t.getValorBD());
                }
            }
        }

        total.setText("R$" + Utils.prepararValor(totalBD));

        switch (totalBD.compareTo(BigDecimal.valueOf(0.0))) {
            case 0:
                total.setTextColor(getResources().getColor(R.color.white));
                break;
            case 1:
                total.setTextColor(getResources().getColor(R.color.rendimento));
                break;
            case -1:
                total.setTextColor(getResources().getColor(R.color.gasto));
        }
    }

    private void configurarRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        transacoes.setLayoutManager(layoutManager);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        transacoes.addItemDecoration(itemDecoration);

        transacoes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && famMenu.getVisibility() == View.VISIBLE) {
                    famMenu.hideMenu(true);
                } else if (dy < 0 && famMenu.getVisibility() != View.VISIBLE) {
                    famMenu.showMenu(true);
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
                        .setMessage("Tem certeza que deseja excluir esta transação?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TransacaoDAO dao = new TransacaoDAO(getBaseContext());
                                int id = (int) adapter.getDbId(POSICAO);
                                int numItens = dao.excluir(id);

                                if (numItens > 0) {
                                    adapter.removerTransacao(POSICAO);
                                    setAdapter("");

                                    Utils.mostrarMensagemCurta(TransacaoActivity.this, "Transação Excluida");
                                    preferencias.salvarStatusSincronia(false);
                                } else {
                                    adapter.cancelarRemocao(POSICAO);
                                    Utils.mostrarMensagemCurta(TransacaoActivity.this, "Não foi possível excluir a transação");
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

        itemTouchHelper.attachToRecyclerView(transacoes);
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    v.getBackground().setColorFilter(getResources().getColor(R.color.cinza), PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    v.getBackground().clearColorFilter();
                    v.invalidate();
                    break;
                }
            }

            return false;
        }
    };
}
