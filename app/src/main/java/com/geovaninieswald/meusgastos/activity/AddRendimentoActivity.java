package com.geovaninieswald.meusgastos.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.fragment.DatePickerFragment;
import com.geovaninieswald.meusgastos.model.Categoria;
import com.geovaninieswald.meusgastos.model.Rendimento;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import faranjit.currency.edittext.CurrencyEditText;

public class AddRendimentoActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private Toolbar toolbar;

    private EditText descricao, categoria, data, quantidade;
    private CurrencyEditText valor;
    private Switch repetir;
    private Button adicionar;
    private ProgressBar carregando;

    private Categoria c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rendimento);

        toolbar = findViewById(R.id.toolbarID);
        toolbar.setTitle("Adicionar Rendimento");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        descricao = findViewById(R.id.descricaoID);
        categoria = findViewById(R.id.categoriaID);
        valor = findViewById(R.id.valorID);
        data = findViewById(R.id.dataID);
        quantidade = findViewById(R.id.quantidadeID);
        repetir = findViewById(R.id.repetirID);
        adicionar = findViewById(R.id.adicionarID);
        carregando = findViewById(R.id.carregandoID);

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

        valor.setText("0");
        data.setText(dataParaString(Calendar.getInstance()));
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

                    if (descricaoStr.trim().isEmpty() || c == null || valorBD.equals(0)) {
                        // Mensagem de erro
                    } else {
                        Rendimento r = new Rendimento();
                        r.setDescricao(descricaoStr);
                        r.setValor(valorBD);
                        r.setData(dataD);
                        r.setQuantidade(quantidadeI);

                        // Salvar novo rendimento!
                    }
                } catch (ParseException e) {
                    // tratar
                } catch (NumberFormatException e) {
                    // tratar
                }

                break;
            case R.id.categoriaID:
                hideSoftKeyboard(v);
                startActivity(new Intent(AddRendimentoActivity.this, CategoriaActivity.class));
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

    private String dataParaString(Calendar c) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(c.getTime());
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
