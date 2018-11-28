package com.geovaninieswald.meusgastos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.helper.SharedFirebasePreferences;
import com.geovaninieswald.meusgastos.model.DAO.ConexaoFirebase;
import com.geovaninieswald.meusgastos.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginEmailActivity extends Activity implements View.OnClickListener {

    private EditText edtEmail, edtSenha;
    private Button btnLogin, btnNovo;
    private ProgressBar carregando;
    private ConstraintLayout containerMeio;
    private ConstraintLayout containerFim;

    private FirebaseAuth autenticacao;
    private SharedFirebasePreferences preferencias;
    private DatabaseReference referenciaDB;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        preferencias = new SharedFirebasePreferences(LoginEmailActivity.this);
        referenciaDB = ConexaoFirebase.getDBReference("usuarios");

        edtEmail = findViewById(R.id.emailID);
        edtSenha = findViewById(R.id.senhaID);
        btnLogin = findViewById(R.id.loginID);
        btnNovo = findViewById(R.id.novoID);
        carregando = findViewById(R.id.carregandoID);
        containerMeio = findViewById(R.id.containerMeioID);
        containerFim = findViewById(R.id.containerFimID);

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

        referenciaDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    usuario = objSnapshot.getValue(Usuario.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void login(String email, String senha) {
        iniciarCarregamento();

        autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(LoginEmailActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = autenticacao.getCurrentUser().getUid();

                    referenciaDB.child(id);
                    usuario.setId(id);

                    preferencias.salvarLogin(usuario);

                    // CARREGAR OS DADOS (FIREBASE) DO USUARIO QUE ENTROU PARA O SQLITE

                    finishAffinity();
                    finish();
                    startActivity(new Intent(LoginEmailActivity.this, MainActivity.class));
                } else {
                    pararCarregamento();

                    alerta("Erro ao logar");
                }
            }
        });
    }

    private void alerta(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void iniciarCarregamento() {
        carregando.setVisibility(View.VISIBLE);

        for (int i = 0; i < containerMeio.getChildCount(); i++) {
            View child = containerMeio.getChildAt(i);
            child.setEnabled(false);
        }

        for (int i = 0; i < containerFim.getChildCount(); i++) {
            View child = containerFim.getChildAt(i);
            child.setEnabled(false);
        }
    }

    private void pararCarregamento() {
        carregando.setVisibility(View.INVISIBLE);

        for (int i = 0; i < containerMeio.getChildCount(); i++) {
            View child = containerMeio.getChildAt(i);
            if (child instanceof TextView || child instanceof ImageView) {
                // Resolver crash
            } else {
                child.setEnabled(true);
            }
        }

        for (int i = 0; i < containerFim.getChildCount(); i++) {
            View child = containerFim.getChildAt(i);
            child.setEnabled(true);
        }
    }
}
