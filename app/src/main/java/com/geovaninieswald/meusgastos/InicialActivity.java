package com.geovaninieswald.meusgastos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InicialActivity extends Activity implements View.OnClickListener {

    private Button btLoginGoogle;
    private Button btLoginFacebook;
    private Button btLoginEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);

        btLoginGoogle = findViewById(R.id.loginGoogleID);
        btLoginFacebook = findViewById(R.id.loginFaceID);
        btLoginEmail = findViewById(R.id.loginEmailID);

        btLoginGoogle.setOnClickListener(this);
        btLoginFacebook.setOnClickListener(this);
        btLoginEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()){
            case R.id.loginGoogleID:
                intent = new Intent(InicialActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.loginFaceID:
                intent = new Intent(InicialActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.loginEmailID:
                intent = new Intent(InicialActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }



    }
}
