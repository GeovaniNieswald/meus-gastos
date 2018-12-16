package com.geovaninieswald.meusgastos.activity;

import android.content.ContentValues;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.geovaninieswald.meusgastos.helper.ItemOffsetDecoration;
import com.geovaninieswald.meusgastos.helper.RecyclerViewRelatorioAdapter;
import com.geovaninieswald.meusgastos.helper.Utils;
import com.geovaninieswald.meusgastos.model.Categoria;
import com.geovaninieswald.meusgastos.model.DAO.TransacaoDAO;
import com.geovaninieswald.meusgastos.model.Transacao;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RelatorioActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView mesAno, info;
    private Button mesAnterior, proximoMes;

    private PieChart grafico;

    private boolean gasto;
    private String tipoTransacao;

    private ConstraintLayout containerGrafico;
    private ScrollView containerScroll;

    private RecyclerView relatorio;
    private RecyclerViewRelatorioAdapter adapter;

    private GregorianCalendar cal;
    private Date mesAnoDate;
    private Locale local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        local = new Locale("pt", "BR");
        cal = new GregorianCalendar();

        containerGrafico = findViewById(R.id.containerGraficoID);
        containerScroll = findViewById(R.id.containerScrollID);
        relatorio = findViewById(R.id.relatorioID);
        info = findViewById(R.id.infoID);
        toolbar = findViewById(R.id.toolbarID);
        grafico = findViewById(R.id.graficoID);
        mesAnterior = findViewById(R.id.mesAnteriorID);
        mesAno = findViewById(R.id.mesAnoID);
        proximoMes = findViewById(R.id.proximoMesID);

        gasto = getIntent().getBooleanExtra("gasto", false);

        if (gasto) {
            tipoTransacao = "Gastos";
        } else {
            tipoTransacao = "Rendimentos";
        }

        toolbar.setTitle("Relatório de " + tipoTransacao);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mesAnterior.setOnClickListener(this);
        mesAnterior.setOnTouchListener(onTouchListener);
        proximoMes.setOnClickListener(this);
        proximoMes.setOnTouchListener(onTouchListener);

        configurarGrafico();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
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
        }
    }

    private void configurarRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        relatorio.setLayoutManager(layoutManager);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset_2);
        relatorio.addItemDecoration(itemDecoration);
    }

    private void configurarGrafico() {
        grafico.setUsePercentValues(true);
        grafico.getDescription().setEnabled(true);
        grafico.setExtraOffsets(0, 0, 0, 0);
        grafico.setDragDecelerationFrictionCoef(0.95f);
        grafico.setDrawHoleEnabled(true);
        grafico.setHoleColor(Color.TRANSPARENT);
        grafico.setDrawCenterText(true);
        grafico.setCenterTextColor(Color.WHITE);
        grafico.setCenterTextSize(16f);
        grafico.setDrawEntryLabels(false);
        grafico.setDescription(null);
        grafico.getLegend().setEnabled(false);
    }

    private void setAdapter() {
        String mes = new SimpleDateFormat("MMMM", local).format(mesAnoDate);
        String ano = new SimpleDateFormat("yyyy", local).format(mesAnoDate);

        mesAno.setText(Utils.primeriaLetraMaiuscula(mes) + " de " + ano);
        grafico.setCenterText(Utils.primeriaLetraMaiuscula(mes));

        try {
            List<Transacao> transacoesList = new TransacaoDAO(this).retornarPorMesAno(mesAnoDate);
            List<Transacao> aux = new ArrayList<>();
            List<ContentValues> contentValuesList = new ArrayList<>();
            Set<Categoria> categorias = new HashSet<>();

            for (Transacao t : transacoesList) {
                if (t.isPago()) {
                    if (gasto) {
                        if (t.getCategoria().getTipoCategoria() != TipoCategoria.RENDIMENTO) {
                            aux.add(t);
                            categorias.add(t.getCategoria());
                        }
                    } else {
                        if (t.getCategoria().getTipoCategoria() != TipoCategoria.GASTO) {
                            aux.add(t);
                            categorias.add(t.getCategoria());
                        }
                    }
                }
            }

            transacoesList = aux;

            if (transacoesList.size() == 0) {
                if (gasto) {
                    info.setText("Você não possui nenhum gasto pago no mês de " + mes);
                } else {
                    info.setText("Você não possui nenhum rendimento pago no mês de " + mes);
                }

                info.setVisibility(View.VISIBLE);
                containerGrafico.setVisibility(View.INVISIBLE);
                containerScroll.setVisibility(View.INVISIBLE);
            } else {
                info.setVisibility(View.INVISIBLE);
                containerGrafico.setVisibility(View.VISIBLE);
                containerScroll.setVisibility(View.VISIBLE);

                ArrayList<PieEntry> values = new ArrayList<>();

                BigDecimal total = new BigDecimal(0.0);

                for (Categoria c : categorias) {
                    BigDecimal valor = new BigDecimal(0.0);

                    for (Transacao t : transacoesList) {
                        if (t.getCategoria().equals(c)) {
                            valor = valor.add(t.getValorBD());
                            total = total.add(t.getValorBD());
                        }
                    }

                    ContentValues cv = new ContentValues();
                    cv.put("descricao", c.getDescricao());
                    cv.put("valor", valor.doubleValue());
                    values.add(new PieEntry(valor.floatValue()));

                    contentValuesList.add(cv);
                }

                List<Integer> cores = setDataGrafico(values);
                List<ContentValues> aux2 = new ArrayList<>();

                int count = 0;

                for (ContentValues cv : contentValuesList) {
                    cv.put("cor", cores.get(count));

                    BigDecimal bdAux = new BigDecimal(((cv.getAsDouble("valor") * 100) / total.doubleValue()));
                    bdAux = bdAux.setScale(2, BigDecimal.ROUND_HALF_UP);
                    double porcentagem = bdAux.doubleValue();

                    cv.put("porcentagem", porcentagem);

                    aux2.add(cv);
                    count++;
                }

                contentValuesList = aux2;

                adapter = new RecyclerViewRelatorioAdapter(RelatorioActivity.this, contentValuesList);
                relatorio.setAdapter(adapter);
            }
        } catch (ParseException e) {
            // Tratar
        }
    }

    private List<Integer> setDataGrafico(ArrayList<PieEntry> values) {
        PieDataSet dataSet = new PieDataSet(values, "");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);
        dataSet.setDrawValues(false);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieData = new PieData(dataSet);

        grafico.setData(pieData);

        grafico.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        return dataSet.getColors();
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
