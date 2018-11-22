package com.geovaninieswald.meusgastos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.geovaninieswald.meusgastos.R;

public class LoginActivity extends Activity implements View.OnClickListener {

    private Button btnLoginGoogle;
    private Button btnLoginFacebook;
    private Button btnLoginEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLoginGoogle = findViewById(R.id.loginGoogleID);
        btnLoginFacebook = findViewById(R.id.loginFaceID);
        btnLoginEmail = findViewById(R.id.loginEmailID);

        btnLoginGoogle.setOnClickListener(this);
        btnLoginFacebook.setOnClickListener(this);
        btnLoginEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.loginGoogleID:
                intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.loginFaceID:
                intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.loginEmailID:
                intent = new Intent(LoginActivity.this, LoginEmailActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
