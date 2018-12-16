package com.geovaninieswald.meusgastos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.helper.SharedFirebasePreferences;
import com.geovaninieswald.meusgastos.helper.Utils;
import com.geovaninieswald.meusgastos.model.DAO.ConexaoFirebase;
import com.geovaninieswald.meusgastos.model.DAO.UsuarioDAO;
import com.geovaninieswald.meusgastos.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
    private List<Usuario> usuarios;

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

        usuarios = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginID:
                if (Utils.estaConectado(LoginEmailActivity.this)) {
                    String email = edtEmail.getText().toString();
                    String senha = edtSenha.getText().toString();

                    if (email.trim().isEmpty() || senha.trim().isEmpty()) {
                        Utils.mostrarMensagemCurta(LoginEmailActivity.this, "Você deve informar um e-mail e senha");
                    } else {
                        login(email, senha);
                    }
                } else {
                    Utils.alertaSimples(LoginEmailActivity.this, "Sem conexão", "Você precisa estar conectado à internet para fazer login!");
                }

                break;
            case R.id.novoID:
                startActivity(new Intent(LoginEmailActivity.this, CadastroActivity.class));
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
                    Usuario user = objSnapshot.getValue(Usuario.class);
                    usuarios.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void login(final String email, String senha) {
        Utils.iniciarCarregamento(carregando, containerMeio, containerFim);

        autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(LoginEmailActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = autenticacao.getCurrentUser().getUid();

                    for (Usuario u : usuarios) {
                        if (u.getId().equals(id)) {
                            usuario = u;
                            break;
                        }
                    }

                    preferencias.salvarLogin(usuario);

                    UsuarioDAO dao = new UsuarioDAO(LoginEmailActivity.this);
                    long retorno = dao.salvar(usuario);

                    if (retorno == -1) {
                        Utils.pararCarregamento(carregando, containerMeio, containerFim);
                        Utils.mostrarMensagemCurta(LoginEmailActivity.this, "Não foi possível efetuar login");
                        ConexaoFirebase.sair();
                        preferencias.sair();
                    } else {
                        // VERIFICAR SE BASE LOCAL ESTÁ SINCRONIZADA COM FIREBASE, CASO NÃO ESTEJA CARREGAR OS DADOS (FIREBASE) DO USUARIO QUE ENTROU PARA O SQLITE

                        preferencias.salvarStatusSincronia(true);
                        finishAffinity();
                        finish();
                        startActivity(new Intent(LoginEmailActivity.this, MainActivity.class));
                    }
                } else {
                    Utils.pararCarregamento(carregando, containerMeio, containerFim);
                    Utils.mostrarMensagemCurta(LoginEmailActivity.this, "Erro ao logar");
                }
            }
        });
    }
}
