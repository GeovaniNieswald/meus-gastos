package com.geovaninieswald.meusgastos.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.geovaninieswald.meusgastos.R;

public class AddRendimentoActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rendimento);

        toolbar = findViewById(R.id.toolbarID);
        toolbar.setTitle("Adicionar Rendimento");
        setSupportActionBar(toolbar);
    }
}
