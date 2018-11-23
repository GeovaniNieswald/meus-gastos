package com.geovaninieswald.meusgastos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.helper.SharedFirebasePreferences;

public class LoginActivity extends Activity implements View.OnClickListener {

    private Button btnLoginGoogle;
    private Button btnLoginFacebook;
    private Button btnLoginEmail;
    private SharedFirebasePreferences sfp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sfp = new SharedFirebasePreferences(LoginActivity.this);

        if(sfp.verificarLogin()){
            //busca dados locais e att servidor
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        btnLoginGoogle = findViewById(R.id.loginGoogleID);
        btnLoginFacebook = findViewById(R.id.loginFaceID);
        btnLoginEmail = findViewById(R.id.loginEmailID);

        btnLoginGoogle.setOnClickListener(this);
        btnLoginFacebook.setOnClickListener(this);
        btnLoginEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginGoogleID:
                // implementar login com goole
                break;
            case R.id.loginFaceID:
                // implementar login com facebook
                break;
            case R.id.loginEmailID:
                startActivity(new Intent(LoginActivity.this, LoginEmailActivity.class));
                finish();
        }
    }
}
