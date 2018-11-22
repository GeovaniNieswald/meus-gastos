package com.geovaninieswald.meusgastos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.model.DAO.ConexaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginEmailActivity extends Activity implements View.OnClickListener {

    private EditText edtEmail, edtSenha;
    private Button btnLogin, btnNovo;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        edtEmail = findViewById(R.id.emailID);
        edtSenha = findViewById(R.id.senhaID);
        btnLogin = findViewById(R.id.loginID);
        btnNovo = findViewById(R.id.novoID);

        btnLogin.setOnClickListener(this);
        btnNovo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // Verificar se campos estão preenchidos
        String email = edtEmail.getText().toString();
        String senha = edtSenha.getText().toString();

        switch (v.getId()) {
            case R.id.loginID:
                login(email, senha);
                break;
            case R.id.novoID:
                criarUsuario(email, senha);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = ConexaoFirebase.getFirebaseAuth();
    }

    private void login(String email, String senha) {
        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(LoginEmailActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginEmailActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    alerta("Erro ao logar");
                }
            }
        });
    }

    private void criarUsuario(String email, String senha) {
        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(LoginEmailActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    alerta("Usuário cadastrado com sucesso");

                    Intent intent = new Intent(LoginEmailActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    alerta("Erro ao cadastrar");
                }
            }
        });
    }

    private void alerta(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
