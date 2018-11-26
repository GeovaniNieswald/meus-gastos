package com.geovaninieswald.meusgastos.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.helper.SharedFirebasePreferences;
import com.geovaninieswald.meusgastos.model.DAO.ConexaoFirebase;
import com.geovaninieswald.meusgastos.model.DAO.UsuarioDAO;
import com.geovaninieswald.meusgastos.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;

import java.io.FileNotFoundException;
import java.io.IOException;

public class CadastroActivity extends Activity implements View.OnClickListener {

    private ImageView icone;
    private EditText edtNome, edtEmail, edtSenha, edtConfirmarSenha;
    private Button btnCadastrar;

    private FirebaseAuth autenticacao;
    private DatabaseReference referencia;
    private SharedFirebasePreferences preferencias;
    private Usuario usuario;

    private int controle;
    private Bitmap bm;

    private final int R_COD_GALERIA = 69;
    private final int R_COD_CAMERA = 70;

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

        referencia = ConexaoFirebase.getFirebase("usuarios");
        preferencias = new SharedFirebasePreferences(CadastroActivity.this);

        icone = findViewById(R.id.iconeID);
        edtNome = findViewById(R.id.nomeID);
        edtEmail = findViewById(R.id.emailID);
        edtSenha = findViewById(R.id.senhaID);
        edtConfirmarSenha = findViewById(R.id.confirmarSenhaID);
        btnCadastrar = findViewById(R.id.cadastrarID);

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
                cadastrarUsuario();
                break;
            case R.id.iconeID:
                selecionarImagem();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == R_COD_GALERIA) {
            if (data != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    salvarImagem(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == R_COD_CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            salvarImagem(thumbnail);
        }
    }

    private void selecionarImagem() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Selecione uma opção");
        CharSequence[] pictureDialogItems = {"Selecione imagem da galeria", "Tire uma foto com a câmera"};
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    escolherGaleria();
                } else if (which == 1) {
                    escolherCamera();
                }
            }
        });

        pictureDialog.show();
    }

    private void escolherGaleria() {
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galeria, R_COD_GALERIA);
    }

    private void escolherCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, R_COD_CAMERA);
    }

    private void salvarImagem(Bitmap bitmap) {
        this.bm = bitmap;

        RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        rbd.setCircular(true);
        icone.setBackground(rbd);
    }

    private void cadastrarUsuario() {
        String nome = edtNome.getText().toString();
        String imagem = "";
        String email = edtEmail.getText().toString();
        String senha = edtSenha.getText().toString();
        String confirmarSenha = edtConfirmarSenha.getText().toString();

        if (nome.trim().isEmpty() || email.trim().isEmpty() || senha.trim().isEmpty() || confirmarSenha.trim().isEmpty()) {
            alerta("Insira todos os dados");
        } else {
            if (senha.equals(confirmarSenha)) {
                if (++controle == 1) {
                    alerta("Você pode escolher uma imagem, basta tocar no icone");
                    icone.setBackground(getDrawable(R.drawable.ic_cadastro_alerta));
                    HANDLER.postDelayed(RUNNABLE, 4000);
                } else {
                    usuario = new Usuario(nome, imagem, email);
                    firebaseAuthWithEmailAndPassword(email, senha);
                }
            } else {
                alerta("Senhas informadas não correspondem");
            }
        }
    }

    private void firebaseAuthWithEmailAndPassword(String email, String senha) {
        autenticacao.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    usuario.setId(autenticacao.getCurrentUser().getUid());
                    preferencias.salvarLogin(usuario);

                    if (referencia.child(usuario.getId()).setValue(usuario).isSuccessful()) {
                        preferencias.salvarStatusSincronia(true);
                    } else {
                        preferencias.salvarStatusSincronia(false);
                    }

                    UsuarioDAO dao = new UsuarioDAO(CadastroActivity.this);
                    dao.salvar(usuario);

                    alerta("Usuário cadastrado com sucesso");

                    startActivity(new Intent(CadastroActivity.this, MainActivity.class));
                    finish();
                } else {
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

                    alerta(erro);
                }
            }
        });
    }

    private void alerta(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
