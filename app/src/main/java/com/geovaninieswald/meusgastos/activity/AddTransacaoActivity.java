package com.geovaninieswald.meusgastos.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.fragment.DatePickerFragment;
import com.geovaninieswald.meusgastos.helper.SharedFirebasePreferences;
import com.geovaninieswald.meusgastos.helper.Utils;
import com.geovaninieswald.meusgastos.model.Categoria;
import com.geovaninieswald.meusgastos.model.DAO.TransacaoDAO;
import com.geovaninieswald.meusgastos.model.Transacao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import faranjit.currency.edittext.CurrencyEditText;

public class AddTransacaoActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private Toolbar toolbar;
    private EditText descricao, categoria, data, quantidade;
    private CheckBox pago;
    private CurrencyEditText valor;
    private Switch repetir;
    private Button adicionar;
    private ProgressBar carregando;
    private ConstraintLayout containerMeio;

    private Categoria c;
    private Transacao transacaoExtra;

    private boolean gasto;
    private boolean alterar;

    private String tipoTransacao;
    private String tipoOperacaoTxt1;
    private String tipoOperacaoTxt2;

    private final int RC_CATEGORIA = 0;
    private final int RC_QRCODE = 1;

    private SharedFirebasePreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transacao);

        descricao = findViewById(R.id.descricaoID);
        quantidade = findViewById(R.id.quantidadeID);
        carregando = findViewById(R.id.carregandoID);
        containerMeio = findViewById(R.id.containerMeioID);
        adicionar = findViewById(R.id.adicionarID);
        toolbar = findViewById(R.id.toolbarID);
        data = findViewById(R.id.dataID);
        data.setText(Utils.dateParaString(new Date()));
        categoria = findViewById(R.id.categoriaID);
        valor = findViewById(R.id.valorID);
        repetir = findViewById(R.id.repetirID);
        pago = findViewById(R.id.pagoID);

        gasto = getIntent().getBooleanExtra("gasto", false);
        alterar = getIntent().getBooleanExtra("alterar", false);

        if (gasto) {
            tipoTransacao = "Gasto";
        } else {
            tipoTransacao = "Rendimento";
        }

        pago.setText(tipoTransacao + " Pago");
        valor.setText("0");

        adicionar.setOnClickListener(this);
        data.setOnClickListener(this);
        categoria.setOnClickListener(this);

        repetir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    quantidade.setVisibility(View.VISIBLE);
                } else {
                    quantidade.setVisibility(View.INVISIBLE);
                }
            }
        });

        if (alterar) {
            tipoOperacaoTxt1 = "alterar ";
            tipoOperacaoTxt2 = "alterado";
            adicionar.setText("Alterar");
            repetir.setEnabled(false);

            transacaoExtra = (Transacao) getIntent().getSerializableExtra("transacao");

            if (transacaoExtra != null) {
                descricao.setText(transacaoExtra.getDescricao());
                c = transacaoExtra.getCategoria();
                categoria.setText(c.getDescricao());

                String valorStr = transacaoExtra.getValorBD().toString();
                valorStr = valorStr.replace(".", ",");

                String[] split = valorStr.split(",");

                if (split[1].toString().length() == 1)
                    valorStr += "0";

                valor.setText(valorStr);

                data.setText(Utils.dateParaString(transacaoExtra.getDataBD()));
                pago.setChecked(transacaoExtra.isPago());
            }
        } else {
            tipoOperacaoTxt1 = "adicionar ";
            tipoOperacaoTxt2 = "adicionado";
            adicionar.setText("Adicionar");
            repetir.setEnabled(true);
        }

        toolbar.setTitle(Utils.primeriaLetraMaiuscula(tipoOperacaoTxt1) + tipoTransacao);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        preferencias = new SharedFirebasePreferences(AddTransacaoActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (gasto) {
            getMenuInflater().inflate(R.menu.menu_gasto_toolbar, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.qr_code:
                startActivityForResult(new Intent(AddTransacaoActivity.this, QrCodeScannerActivity.class), RC_QRCODE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.adicionarID:
                try {
                    String descricaoStr = descricao.getText().toString();
                    BigDecimal valorBD = BigDecimal.valueOf(valor.getCurrencyDouble());
                    Date dataD = Utils.stringParaDate(data.getText().toString());
                    int quantidadeI;

                    if (repetir.isChecked()) {
                        quantidadeI = Integer.parseInt(quantidade.getText().toString());
                    } else {
                        quantidadeI = 1;
                    }

                    if (descricaoStr.trim().isEmpty() || c == null || valorBD.equals(BigDecimal.valueOf(0.0))) {
                        Utils.mostrarMensagemCurta(AddTransacaoActivity.this, "Informe todos os dados");
                    } else {
                        Utils.iniciarCarregamento(carregando, containerMeio);

                        Transacao t = new Transacao();
                        t.setDescricao(descricaoStr);
                        t.setCategoria(c);
                        t.setValorBD(valorBD);
                        t.setDataBD(dataD);
                        t.setQuantidade(quantidadeI);
                        t.setPago(pago.isChecked());

                        TransacaoDAO dao = new TransacaoDAO(AddTransacaoActivity.this);
                        List<Long> retorno = new ArrayList<>();

                        if (alterar) {
                            t.setId(transacaoExtra.getId());
                            retorno.add(dao.alterar(t));
                        } else {
                            retorno = dao.salvar(t);
                        }

                        boolean erro = false;
                        int sucesso = 0;

                        for (long l : retorno) {
                            if (l == -2) {
                                erro = true;
                            } else if (l == -1) {
                                erro = true;
                            } else {
                                sucesso++;
                            }
                        }

                        Utils.pararCarregamento(carregando, containerMeio);

                        if (erro) {
                            String texto;
                            if (quantidadeI == 1) {
                                texto = "Não foi possível " + tipoOperacaoTxt1 + "o " + tipoTransacao;
                            } else {
                                texto = "Não foi possível " + tipoOperacaoTxt1 + "algum " + tipoTransacao;
                            }
                            Utils.mostrarMensagemCurta(AddTransacaoActivity.this, texto);
                        } else {
                            String texto;
                            if (quantidadeI == 1) {
                                texto = tipoTransacao + " " + tipoOperacaoTxt2 + " com sucesso";
                            } else {
                                texto = tipoTransacao + "s " + tipoOperacaoTxt2 + "s com sucesso";
                            }
                            Utils.mostrarMensagemCurta(AddTransacaoActivity.this, texto);
                        }

                        if (sucesso >= 1) {
                            preferencias.salvarStatusSincronia(false);

                            if (alterar) {
                                finish();
                            } else {
                                descricao.setText("");
                                c = null;
                                categoria.setText("Selecionar");
                                valor.setText("0");
                                data.setText(Utils.dateParaString(new Date()));
                                quantidade.setText("");
                                repetir.setChecked(false);
                                pago.setChecked(false);
                            }
                        }
                    }
                } catch (ParseException e) {
                    Utils.mostrarMensagemCurta(AddTransacaoActivity.this, "Não foi possível " + tipoOperacaoTxt1 + "o " + tipoTransacao);
                } catch (NumberFormatException e) {
                    Utils.mostrarMensagemCurta(AddTransacaoActivity.this, "Informe a quantidade");
                }

                break;
            case R.id.categoriaID:
                Utils.hideSoftKeyboard(AddTransacaoActivity.this, v);

                Intent intent = new Intent(AddTransacaoActivity.this, CategoriaActivity.class);
                intent.putExtra("transacao", true);
                intent.putExtra("gasto", gasto);
                startActivityForResult(intent, RC_CATEGORIA);

                break;
            case R.id.dataID:
                try {
                    Utils.hideSoftKeyboard(AddTransacaoActivity.this, v);

                    DialogFragment datePicker = new DatePickerFragment();
                    ((DatePickerFragment) datePicker).setData(Utils.stringParaDate(data.getText().toString()));

                    datePicker.show(getSupportFragmentManager(), "date picker");
                } catch (ParseException e) {
                    // Tratar
                }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        data.setText(Utils.dateParaString(c.getTime()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_CATEGORIA:
                    c = (Categoria) data.getSerializableExtra("categoria");
                    categoria.setText(c.getDescricao());
                    break;
                case RC_QRCODE:
                    String retorno = data.getStringExtra("data");
                    Utils.mostrarMensagemCurta(AddTransacaoActivity.this, retorno);
                    // pegar dados do qr code lido e colocar nos campos
            }
        }
    }
}
