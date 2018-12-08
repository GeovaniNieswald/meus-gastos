package com.geovaninieswald.meusgastos.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.helper.SharedFirebasePreferences;
import com.geovaninieswald.meusgastos.helper.Utils;
import com.geovaninieswald.meusgastos.model.DAO.ConexaoFirebase;
import com.geovaninieswald.meusgastos.model.DAO.UsuarioDAO;
import com.geovaninieswald.meusgastos.model.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends Activity implements View.OnClickListener {

    private Button btnLoginGoogle, btnLoginFacebook, btnLoginEmail;
    private ProgressBar carregando;
    private ConstraintLayout containerMeio;

    private SharedFirebasePreferences preferencias;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;
    private FirebaseAuth autenticacao;
    private DatabaseReference referenciaDB;
    private Usuario usuario;

    private final int COD_GOOGLE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferencias = new SharedFirebasePreferences(LoginActivity.this);
        referenciaDB = ConexaoFirebase.getDBReference("usuarios");

        if (preferencias.verificarLogin()) {
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        callbackManager = CallbackManager.Factory.create();

        keyHash(); //Remover depois

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Utils.mostrarMensagemCurta(LoginActivity.this, "Login cancelado");
            }

            @Override
            public void onError(FacebookException exception) {
                Utils.mostrarMensagemCurta(LoginActivity.this, "Erro ao fazer login");
            }
        });

        btnLoginGoogle = findViewById(R.id.loginGoogleID);
        btnLoginFacebook = findViewById(R.id.loginFaceID);
        btnLoginEmail = findViewById(R.id.loginEmailID);
        carregando = findViewById(R.id.carregandoID);
        containerMeio = findViewById(R.id.containerMeioID);

        btnLoginGoogle.setOnClickListener(this);
        btnLoginFacebook.setOnClickListener(this);
        btnLoginEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginGoogleID:
                startActivityForResult(mGoogleSignInClient.getSignInIntent(), COD_GOOGLE);
                break;
            case R.id.loginFaceID:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
                break;
            case R.id.loginEmailID:
                startActivity(new Intent(LoginActivity.this, LoginEmailActivity.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        autenticacao = ConexaoFirebase.getFirebaseAuth();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COD_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                firebaseAuthWithGoogle(task.getResult(ApiException.class));
            } catch (ApiException e) {
                Utils.mostrarMensagemCurta(LoginActivity.this, "Erro ao fazer login");
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        login(GoogleAuthProvider.getCredential(acct.getIdToken(), null));
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        login(FacebookAuthProvider.getCredential(token.getToken()));
    }

    private void login(AuthCredential credential) {
        Utils.iniciarCarregamento(carregando, containerMeio);

        autenticacao.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser usuarioFirebase = autenticacao.getCurrentUser();

                    String urlImagem = usuarioFirebase.getPhotoUrl().toString();

                    for (UserInfo profile : usuarioFirebase.getProviderData()) {
                        if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                            urlImagem = "https://graph.facebook.com/" + profile.getUid() + "/picture?height=70";
                        } else if (GoogleAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                            urlImagem = urlImagem.replace("/s96-c/", "/s70-c/");
                        }
                    }

                    usuario = new Usuario(usuarioFirebase.getDisplayName(), urlImagem, usuarioFirebase.getEmail());
                    usuario.setId(usuarioFirebase.getUid());

                    preferencias.salvarLogin(usuario);

                    referenciaDB.child(usuario.getId()).setValue(usuario).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> pao) {
                            if (pao.isSuccessful()) {
                                UsuarioDAO dao = new UsuarioDAO(LoginActivity.this);
                                long retorno = dao.salvar(usuario);

                                if (retorno == -1) {
                                    Utils.pararCarregamento(carregando, containerMeio);
                                    Utils.mostrarMensagemCurta(LoginActivity.this, "Não foi possível efetuar login");
                                    ConexaoFirebase.sair();
                                    preferencias.sair();
                                } else {
                                    // VERIFICAR SE BASE LOCAL ESTÁ SINCRONIZADA COM FIREBASE, CASO NÃO ESTEJA CARREGAR OS DADOS (FIREBASE) DO USUARIO QUE ENTROU PARA O SQLITE

                                    finish();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                            } else {
                                Utils.mostrarMensagemCurta(LoginActivity.this, "Não foi possível efetuar login");
                                ConexaoFirebase.sair();
                                preferencias.sair();
                            }
                        }
                    });
                } else {
                    Utils.pararCarregamento(carregando, containerMeio);

                    String erro = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "Este e-mail está cadastrado como uma conta google";
                    } catch (Exception e) {
                        erro = "Erro ao fazer login";
                    }

                    Utils.mostrarMensagemCurta(LoginActivity.this, erro);
                }
            }
        });
    }

    private void keyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.geovaninieswald.meusgastos", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
        }
    }
}
