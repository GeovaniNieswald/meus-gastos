package com.geovaninieswald.meusgastos.activity;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.fragment.DatePickerFragment;
import com.geovaninieswald.meusgastos.model.Categoria;
import com.geovaninieswald.meusgastos.model.DAO.TransacaoDAO;
import com.geovaninieswald.meusgastos.model.Transacao;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import faranjit.currency.edittext.CurrencyEditText;

public class AddTransacaoActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private Toolbar toolbar;
    private EditText descricao, categoria, data, quantidade;
    private CheckBox paga;
    private CurrencyEditText valor;
    private Switch repetir;
    private Button adicionar;
    private ProgressBar carregando;
    private ConstraintLayout containerMeio;

    private Categoria c;

    private boolean gasto;
    private String tipoTransacao;

    static final int RC_CATEGORIA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transacao);

        gasto = getIntent().getBooleanExtra("gasto", false);

        toolbar = findViewById(R.id.toolbarID);

        if (gasto) {
            tipoTransacao = "Gasto";
        } else {
            tipoTransacao = "Rendimento";
        }

        toolbar.setTitle("Adicionar " + tipoTransacao);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        paga = findViewById(R.id.pagaID);
        paga.setText(tipoTransacao + " Pago");

        descricao = findViewById(R.id.descricaoID);
        quantidade = findViewById(R.id.quantidadeID);

        carregando = findViewById(R.id.carregandoID);
        containerMeio = findViewById(R.id.containerMeioID);

        adicionar = findViewById(R.id.adicionarID);
        adicionar.setOnClickListener(this);

        data = findViewById(R.id.dataID);
        data.setOnClickListener(this);
        data.setText(dataParaString(Calendar.getInstance()));

        categoria = findViewById(R.id.categoriaID);
        categoria.setOnClickListener(this);

        repetir = findViewById(R.id.repetirID);
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

        valor = findViewById(R.id.valorID);
        valor.setText("0");
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
                // Colocar aqui a parte do QR Code
                Toast.makeText(this, "hue", Toast.LENGTH_SHORT).show();
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
                    Date dataD = new SimpleDateFormat("dd/MM/yyyy").parse(data.getText().toString());
                    int quantidadeI;

                    if (repetir.isChecked()) {
                        quantidadeI = Integer.parseInt(quantidade.getText().toString());
                    } else {
                        quantidadeI = 1;
                    }

                    if (descricaoStr.trim().isEmpty() || c == null || valorBD.equals(BigDecimal.valueOf(0.0))) {
                        Toast.makeText(AddTransacaoActivity.this, "Informe todos os dados", Toast.LENGTH_SHORT).show();
                    } else {
                        iniciarCarregamento();

                        Transacao r = new Transacao();
                        r.setDescricao(descricaoStr);
                        r.setCategoria(c);
                        r.setValor(valorBD);
                        r.setData(dataD);
                        r.setQuantidade(quantidadeI);
                        r.setPaga(paga.isChecked());

                        TransacaoDAO dao = new TransacaoDAO(AddTransacaoActivity.this);
                        long retorno = dao.salvar(r);

                        if (retorno == -2) {
                            pararCarregamento();
                            Toast.makeText(AddTransacaoActivity.this, tipoTransacao + " já existe", Toast.LENGTH_SHORT).show();
                        } else if (retorno == -1) {
                            pararCarregamento();
                            Toast.makeText(AddTransacaoActivity.this, "Não foi possível adicionar o " + tipoTransacao, Toast.LENGTH_SHORT).show();
                        } else {
                            pararCarregamento();

                            descricao.setText("");
                            c = null;
                            categoria.setText("");
                            valor.setText("0");
                            data.setText(dataParaString(Calendar.getInstance()));
                            quantidade.setText("");
                            repetir.setChecked(false);
                            paga.setChecked(false);

                            Toast.makeText(AddTransacaoActivity.this, tipoTransacao + " adicionado com sucesso", Toast.LENGTH_SHORT).show();
                            // enviar para firebase alterar sharedPreferences sobre sincronização
                        }
                    }
                } catch (ParseException e) {
                    Toast.makeText(AddTransacaoActivity.this, "Não foi possível adicionar o " + tipoTransacao, Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(AddTransacaoActivity.this, "Informe a quantidade", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.categoriaID:
                hideSoftKeyboard(v);

                Intent intent = new Intent(AddTransacaoActivity.this, CategoriaActivity.class);
                intent.putExtra("transacao", true);
                intent.putExtra("gasto", gasto);
                startActivityForResult(intent, RC_CATEGORIA);

                break;
            case R.id.dataID:
                hideSoftKeyboard(v);
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        data.setText(dataParaString(c));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RC_CATEGORIA == requestCode) {
            if (resultCode == RESULT_OK) {
                c = (Categoria) data.getSerializableExtra("categoria");
                categoria.setText(c.getDescricao());
            }
        }
    }

    private String dataParaString(Calendar c) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(c.getTime());
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
