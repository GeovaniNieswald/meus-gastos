package com.geovaninieswald.meusgastos.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.geovaninieswald.meusgastos.helper.ItemOffsetDecoration;
import com.geovaninieswald.meusgastos.helper.RecyclerViewTransacaoAdapter;
import com.geovaninieswald.meusgastos.helper.SharedFirebasePreferences;
import com.geovaninieswald.meusgastos.helper.Utils;
import com.geovaninieswald.meusgastos.model.DAO.ConexaoFirebase;
import com.geovaninieswald.meusgastos.model.DAO.TransacaoDAO;
import com.geovaninieswald.meusgastos.model.Transacao;
import com.geovaninieswald.meusgastos.model.Usuario;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Button mesAnterior, proximoMes;
    private ImageView imagem;
    private TextView nome, email, mesAno, saldo, valorSaldoDia, saldoDia, gastos, rendimentos, info;
    private FloatingActionMenu famMenu;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private com.github.clans.fab.FloatingActionButton fabReceita, fabGasto;

    private ConstraintLayout containerBaixo;
    private ScrollView containerScroll;

    private RecyclerView transacoes;
    private RecyclerViewTransacaoAdapter adapter;

    private Usuario usuario;
    private SharedFirebasePreferences preferencias;

    private GregorianCalendar cal;
    private Date mesAnoDate;
    private Locale local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        local = new Locale("pt", "BR");
        cal = new GregorianCalendar();

        containerScroll = findViewById(R.id.containerScrollID);
        valorSaldoDia = findViewById(R.id.valorSaldoDiaID);
        saldoDia = findViewById(R.id.saldoDiaID);
        info = findViewById(R.id.infoID);
        containerBaixo = findViewById(R.id.containerBaixoID);
        gastos = findViewById(R.id.gastosID);
        rendimentos = findViewById(R.id.rendimentosID);
        saldo = findViewById(R.id.saldoID);
        transacoes = findViewById(R.id.transacoesID);
        mesAnterior = findViewById(R.id.mesAnteriorID);
        proximoMes = findViewById(R.id.proximoMesID);
        mesAno = findViewById(R.id.mesAnoID);
        toolbar = findViewById(R.id.toolbarID);
        famMenu = findViewById(R.id.famMenuID);
        fabReceita = findViewById(R.id.fabRendimentoID);
        fabGasto = findViewById(R.id.fabGastoID);
        drawer = findViewById(R.id.drawerLayoutID);
        navigationView = findViewById(R.id.navViewID);

        setSupportActionBar(toolbar);

        fabGasto.setOnClickListener(this);
        fabReceita.setOnClickListener(this);
        proximoMes.setOnClickListener(this);
        mesAnterior.setOnClickListener(this);
        mesAnterior.setOnTouchListener(onTouchListener);
        proximoMes.setOnTouchListener(onTouchListener);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        imagem = headerView.findViewById(R.id.imagemID);
        nome = headerView.findViewById(R.id.nomeID);
        email = headerView.findViewById(R.id.emailID);

        preferencias = new SharedFirebasePreferences(MainActivity.this);
        usuario = preferencias.usuarioLogado();

        nome.setText(usuario.getNome());
        email.setText(usuario.getEmail());

        if (usuario.getImagem() != null) {
            Bitmap bm = BitmapFactory.decodeFile(getFilesDir().getAbsolutePath() + "/imagem.jpg");
            RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(), bm);
            rbd.setCircular(true);
            imagem.setBackground(rbd);
        }

        // Esconder fab quando descer scroll, mostrar quando subir

        configurarRecycler();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mesAnoDate = new Date();
        cal.setTime(mesAnoDate);

        setAdapter();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // ADICIONAR OPÇÃO SINCRONIZAR

        switch (item.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_transacoes:
                startActivity(new Intent(MainActivity.this, TransacaoActivity.class));
                break;
            case R.id.nav_categorias:
                startActivity(new Intent(MainActivity.this, CategoriaActivity.class));
                break;
            case R.id.nav_rel_redimentos:
                break;
            case R.id.nav_rel_gastos:
                break;
            case R.id.nav_rel_calendario:
                break;
            case R.id.nav_compartilhar:
                break;
            case R.id.nav_avaliar:
                break;
            case R.id.nav_sobre:
                break;
            case R.id.nav_sair:
                // VERIFICAR SE ESTÁ SINCRONIZADO, CASO NÃO, AVISAR QUE O USUÁRIO PERDERÁ DADOS, CONFIRMAÇÃO EM AMBOS OS CASOS

                preferencias.sair();
                ConexaoFirebase.sair();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawerLayoutID);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

                setAdapter();
                break;
            case R.id.proximoMesID:
                if (cal.get(Calendar.MONTH) == Calendar.DECEMBER) {
                    cal.roll(Calendar.MONTH, true);
                    cal.roll(Calendar.YEAR, true);
                } else {
                    cal.roll(Calendar.MONTH, true);
                }

                mesAnoDate = cal.getTime();

                setAdapter();
                break;
            case R.id.fabGastoID:
                famMenu.close(true);
                startActivity(new Intent(MainActivity.this, AddTransacaoActivity.class).putExtra("gasto", true));
                break;
            case R.id.fabRendimentoID:
                famMenu.close(true);
                startActivity(new Intent(MainActivity.this, AddTransacaoActivity.class).putExtra("gasto", false));
        }
    }

    private void setAdapter() {
        mesAno.setText(Utils.primeriaLetraMaiuscula(new SimpleDateFormat("MMMM 'de' yyyy", local).format(mesAnoDate)));

        List<Transacao> transacoesDia = new ArrayList<>();

        Calendar atual = Calendar.getInstance();
        Calendar alvo = Calendar.getInstance();
        atual.setTime(new Date());
        alvo.setTime(mesAnoDate);

        boolean mesmoDiaMes = atual.get(Calendar.DAY_OF_YEAR) == alvo.get(Calendar.DAY_OF_YEAR) && atual.get(Calendar.YEAR) == alvo.get(Calendar.YEAR);

        try {
            List<Transacao> transacoesList = new TransacaoDAO(this).retornarPorMesAno(mesAnoDate);

            BigDecimal valorRendimentos = BigDecimal.valueOf(0.0);
            BigDecimal valorGastos = BigDecimal.valueOf(0.0);
            BigDecimal valorTotal;

            for (Transacao t : transacoesList) {
                alvo.setTime(t.getData());
                Boolean mesmoDia = atual.get(Calendar.DAY_OF_YEAR) == alvo.get(Calendar.DAY_OF_YEAR) && atual.get(Calendar.YEAR) == alvo.get(Calendar.YEAR);

                if (mesmoDia) {
                    transacoesDia.add(t);
                }

                if (t.isPago()) {
                    if (t.getCategoria().getTipoCategoria() == TipoCategoria.GASTO) {
                        valorGastos = valorGastos.add(t.getValor());
                    } else if (t.getCategoria().getTipoCategoria() == TipoCategoria.RENDIMENTO) {
                        valorRendimentos = valorRendimentos.add(t.getValor());
                    }
                }
            }

            valorTotal = valorRendimentos.subtract(valorGastos);

            saldo.setText("R$" + Utils.prepararValor(valorTotal));
            gastos.setText("R$" + Utils.prepararValor(valorGastos));
            rendimentos.setText("R$" + Utils.prepararValor(valorRendimentos));

            switch (valorTotal.compareTo(BigDecimal.valueOf(0.0))) {
                case 0:
                    saldo.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 1:
                    saldo.setTextColor(getResources().getColor(R.color.rendimento));
                    break;
                case -1:
                    saldo.setTextColor(getResources().getColor(R.color.gasto));
            }

            if (mesmoDiaMes) {
                containerBaixo.setVisibility(View.VISIBLE);

                if (transacoesDia.size() == 0) {
                    info.setVisibility(View.VISIBLE);
                    saldoDia.setVisibility(View.INVISIBLE);
                    valorSaldoDia.setVisibility(View.INVISIBLE);
                } else {
                    info.setVisibility(View.INVISIBLE);
                    saldoDia.setVisibility(View.VISIBLE);
                    valorSaldoDia.setVisibility(View.VISIBLE);

                    valorRendimentos = BigDecimal.valueOf(0.0);
                    valorGastos = BigDecimal.valueOf(0.0);

                    for (Transacao t : transacoesDia) {
                        if (t.isPago()) {
                            if (t.getCategoria().getTipoCategoria() == TipoCategoria.GASTO) {
                                valorGastos = valorGastos.add(t.getValor());
                            } else if (t.getCategoria().getTipoCategoria() == TipoCategoria.RENDIMENTO) {
                                valorRendimentos = valorRendimentos.add(t.getValor());
                            }
                        }
                    }

                    valorTotal = valorRendimentos.subtract(valorGastos);

                    valorSaldoDia.setText("R$" + Utils.prepararValor(valorTotal));

                    switch (valorTotal.compareTo(BigDecimal.valueOf(0.0))) {
                        case 0:
                            valorSaldoDia.setTextColor(getResources().getColor(R.color.white));
                            break;
                        case 1:
                            valorSaldoDia.setTextColor(getResources().getColor(R.color.rendimento));
                            break;
                        case -1:
                            valorSaldoDia.setTextColor(getResources().getColor(R.color.gasto));
                    }

                    adapter = new RecyclerViewTransacaoAdapter(MainActivity.this, transacoesDia);
                    transacoes.setAdapter(adapter);
                }
            } else {
                containerBaixo.setVisibility(View.INVISIBLE);
            }
        } catch (ParseException e) {
            // Tratar
        }
    }

    private void configurarRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        transacoes.setLayoutManager(layoutManager);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        transacoes.addItemDecoration(itemDecoration);

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
                                int numItens = dao.excluir(adapter.getDbId(POSICAO));

                                if (numItens > 0) {
                                    adapter.removerTransacao(POSICAO);
                                    setAdapter();
                                    // Excluir do firebase
                                    Toast.makeText(MainActivity.this, "Transação Excluida", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Não foi possível excluir a transação", Toast.LENGTH_SHORT).show();
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
