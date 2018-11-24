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
import com.geovaninieswald.meusgastos.helper.SharedFirebasePreferences;
import com.geovaninieswald.meusgastos.model.DAO.ConexaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginEmailActivity extends Activity implements View.OnClickListener {

    private EditText edtEmail, edtSenha;
    private Button btnLogin, btnNovo;

    private FirebaseAuth autenticacao;
    SharedFirebasePreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        preferencias = new SharedFirebasePreferences(LoginEmailActivity.this);

        edtEmail = findViewById(R.id.emailID);
        edtSenha = findViewById(R.id.senhaID);
        btnLogin = findViewById(R.id.loginID);
        btnNovo = findViewById(R.id.novoID);

        btnLogin.setOnClickListener(this);
        btnNovo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginID:
                String email = edtEmail.getText().toString();
                String senha = edtSenha.getText().toString();

                if (email.trim().isEmpty() || senha.trim().isEmpty()) {
                    alerta("Você deve informar um e-mail e senha");
                } else {
                    login(email, senha);
                }

                break;
            case R.id.novoID:
                Intent intent = new Intent(LoginEmailActivity.this, CadastroActivity.class);
                startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        autenticacao = ConexaoFirebase.getFirebaseAuth();
    }

    private void login(final String email, String senha) {
        autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(LoginEmailActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    preferencias.salvarLogin(autenticacao.getCurrentUser().getUid());

                    // CARREGAR OS DADOS (FIREBASE) DO USUARIO QUE ENTROU PARA O SQLITE

                    startActivity(new Intent(LoginEmailActivity.this, MainActivity.class));
                    finish();
                } else {
                    alerta("Erro ao logar");
                }
            }
        });
    }

    private void alerta(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
