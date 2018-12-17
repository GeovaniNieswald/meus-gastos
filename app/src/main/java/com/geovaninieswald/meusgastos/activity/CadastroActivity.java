package com.geovaninieswald.meusgastos.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.helper.SharedFirebasePreferences;
import com.geovaninieswald.meusgastos.helper.Utils;
import com.geovaninieswald.meusgastos.model.DAO.ConexaoFirebase;
import com.geovaninieswald.meusgastos.model.DAO.UsuarioDAO;
import com.geovaninieswald.meusgastos.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class CadastroActivity extends Activity implements View.OnClickListener {

    private ImageView icone;
    private EditText edtNome, edtEmail, edtSenha, edtConfirmarSenha;
    private Button btnCadastrar;
    private ProgressBar carregando;
    private ConstraintLayout containerMeio;

    private FirebaseAuth autenticacao;
    private DatabaseReference referenciaDB;
    private StorageReference referenciaST;
    private SharedFirebasePreferences preferencias;

    private Usuario usuario;
    private int controle;
    private Uri uriImagem;

    private final Handler HANDLER = new Handler();
    private final Runnable RUNNABLE = new Runnable() {
        @Override
        public void run() {
            icone.setBackground(getDrawable(R.drawable.ic_cadastro));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        referenciaDB = ConexaoFirebase.getDBReference("usuarios");
        preferencias = new SharedFirebasePreferences(CadastroActivity.this);

        icone = findViewById(R.id.iconeID);
        edtNome = findViewById(R.id.nomeID);
        edtEmail = findViewById(R.id.emailID);
        edtSenha = findViewById(R.id.senhaID);
        edtConfirmarSenha = findViewById(R.id.confirmarSenhaID);
        btnCadastrar = findViewById(R.id.cadastrarID);
        carregando = findViewById(R.id.carregandoID);
        containerMeio = findViewById(R.id.containerMeioID);

        btnCadastrar.setOnClickListener(this);
        icone.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        controle = 0;

        autenticacao = ConexaoFirebase.getFirebaseAuth();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cadastrarID:
                if (Utils.estaConectado(CadastroActivity.this)){
                    cadastrarUsuario();
                } else{
                    Utils.alertaSimples(CadastroActivity.this, "Sem conexão", "Você precisa estar conectado à internet para realizar o cadastro!");
                }
                break;
            case R.id.iconeID:
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setFixAspectRatio(true)
                        .start(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();

                uriImagem = resultUri;

                try {
                    exibirImagem(MediaStore.Images.Media.getBitmap(getContentResolver(), uriImagem));
                } catch (IOException e) {
                    // Tratar
                }
            }
        }
    }

    private void exibirImagem(Bitmap bitmap) {
        RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        rbd.setCircular(true);
        icone.setBackground(rbd);
    }

    private void cadastrarUsuario() {
        String nome = edtNome.getText().toString();
        String email = edtEmail.getText().toString();
        String senha = edtSenha.getText().toString();
        String confirmarSenha = edtConfirmarSenha.getText().toString();

        if (nome.trim().isEmpty() || email.trim().isEmpty() || senha.trim().isEmpty() || confirmarSenha.trim().isEmpty()) {
            Utils.mostrarMensagemCurta(CadastroActivity.this, "Insira todos os dados");
        } else {
            if (senha.equals(confirmarSenha)) {
                if (++controle == 1 && uriImagem == null) {
                    Utils.mostrarMensagemCurta(CadastroActivity.this, "Você pode escolher uma imagem, basta tocar no icone");
                    icone.setBackground(getDrawable(R.drawable.ic_cadastro_alerta));
                    HANDLER.postDelayed(RUNNABLE, 4000);
                } else {
                    usuario = new Usuario();
                    usuario.setNome(nome);
                    usuario.setEmail(email);
                    firebaseAuthWithEmailAndPassword(email, senha);
                }
            } else {
                Utils.mostrarMensagemCurta(CadastroActivity.this, "Senhas informadas não correspondem");
            }
        }
    }

    private void firebaseAuthWithEmailAndPassword(String email, String senha) {
        Utils.iniciarCarregamento(carregando, containerMeio);

        autenticacao.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    enviarImagem();
                } else {
                    Utils.pararCarregamento(carregando, containerMeio);

                    String erro = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Senha muito fraca";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "O e-mail digitado é inválido";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "Esse e-mail já está cadastrado";
                    } catch (Exception e) {
                        erro = "Erro ao cadastrar";
                    }

                    Utils.mostrarMensagemCurta(CadastroActivity.this, erro);
                }
            }
        });
    }

    private void enviarImagem() {
        if (uriImagem == null) {
            salvarUsuario();
        } else {
            referenciaST = ConexaoFirebase.getSTReference("usuarios/" + autenticacao.getCurrentUser().getUid());
            referenciaST = referenciaST.child("imagem.jpg");

            referenciaST.putFile(uriImagem).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    referenciaST.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            usuario.setImagem(uri.toString());
                            salvarUsuario();
                        }
                    });
                }
            });
        }
    }

    private void salvarUsuario() {
        usuario.setId(autenticacao.getCurrentUser().getUid());
        preferencias.salvarLogin(usuario);

        referenciaDB.child(usuario.getId()).setValue(usuario).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    preferencias.salvarStatusSincronia(true);

                    UsuarioDAO dao = new UsuarioDAO(CadastroActivity.this);
                    long retorno = dao.salvar(usuario);

                    if (retorno == -1) {
                        Utils.pararCarregamento(carregando, containerMeio);
                        Utils.mostrarMensagemCurta(CadastroActivity.this, "Não foi possível efetuar o cadastro");
                        ConexaoFirebase.sair();
                        preferencias.sair();
                    } else {
                        startActivity(new Intent(CadastroActivity.this, MainActivity.class));
                        finishAffinity();
                        finish();
                    }
                } else {
                    Utils.mostrarMensagemCurta(CadastroActivity.this, "Não foi possível efetuar o cadastro");
                    ConexaoFirebase.sair();
                    preferencias.sair();
                }
            }
        });
    }
}
