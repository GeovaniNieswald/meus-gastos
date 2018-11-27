package com.geovaninieswald.meusgastos.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.geovaninieswald.meusgastos.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class SharedFirebasePreferences {

    private SharedPreferences preferencias;
    private final String NOME_ARQUIVO = "meus-gastos.preferencias";
    private final int MODO = Context.MODE_PRIVATE;
    private SharedPreferences.Editor editor;
    private Context context;

    private final String CHAVE_ID = "id";
    private final String CHAVE_EMAIL = "email";
    private final String CHAVE_NOME = "nome";
    private final String CHAVE_IMAGEM = "imagem";

    private final String CHAVE_STATUS = "status";

    public SharedFirebasePreferences(Context context) {
        preferencias = context.getSharedPreferences(NOME_ARQUIVO, MODO);
        editor = preferencias.edit();
        this.context = context;
    }

    public void salvarLogin(Usuario usuario) {
        editor.putString(CHAVE_ID, usuario.getId());
        editor.putString(CHAVE_EMAIL, usuario.getEmail());
        editor.putString(CHAVE_NOME, usuario.getNome());

        try {
            if (new BaixarImagem().execute(usuario.getImagem()).get()) {
                editor.putBoolean(CHAVE_IMAGEM, true);
            } else {
                editor.putBoolean(CHAVE_IMAGEM, false);
            }
        } catch (ExecutionException | InterruptedException e) {
            editor.putBoolean(CHAVE_IMAGEM, false);
        }

        editor.commit();
    }

    public Usuario usuarioLogado() {
        Usuario usuario = new Usuario();

        usuario.setId(preferencias.getString(CHAVE_ID, "null"));
        usuario.setNome(preferencias.getString(CHAVE_NOME, "sem nome"));
        usuario.setEmail(preferencias.getString(CHAVE_EMAIL, "sem e-mail"));

        if (preferencias.getBoolean(CHAVE_IMAGEM, false))
            usuario.setImagem("imagem.png");

        return usuario;
    }

    public void salvarStatusSincronia(boolean status) {
        editor.putBoolean(CHAVE_STATUS, status);
        editor.commit();
    }

    public boolean verificarLogin() {
        return preferencias.contains("id");
    }

    public boolean verificarStatusSincronia() {
        return preferencias.getBoolean(CHAVE_STATUS, false);
    }

    public void sair() {
        editor.clear();
        editor.commit();
    }

    private class BaixarImagem extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            boolean retornoOk = true;

            if (strings[0].trim().isEmpty())
                return false;

            try {
                URL url = new URL(strings[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(input);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] bArr = bos.toByteArray();
                bos.flush();
                bos.close();

                FileOutputStream fos = context.openFileOutput("imagem.jpg", Context.MODE_PRIVATE);
                fos.write(bArr);
                fos.flush();
                fos.close();
            } catch (MalformedURLException e) {
                retornoOk = false;
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                retornoOk = false;
                e.printStackTrace();
            } catch (IOException e) {
                retornoOk = false;
                e.printStackTrace();
            }

            return retornoOk;
        }
    }
}
