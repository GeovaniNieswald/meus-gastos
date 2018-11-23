package com.geovaninieswald.meusgastos.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;

public class SharedFirebasePreferences {

    private SharedPreferences preferencias;
    private final String NOME_ARQUIVO = "meus-gastos.preferencias";
    private final int MODO = Context.MODE_PRIVATE;
    private SharedPreferences.Editor editor;

    private final String CHAVE_ID = "id";
    //private final String CHAVE_EMAIL = "email";

    public SharedFirebasePreferences(Context context) {
        preferencias = context.getSharedPreferences(NOME_ARQUIVO, MODO);
        editor = preferencias.edit();
    }

    public void salvarUsuario(String id) {
        editor.putString(CHAVE_ID, id);
        // editor.putString(CHAVE_EMAIL, email);
        editor.commit();
    }

    public boolean verificarLogin() {
        return preferencias.contains("id");
    }

    public void sair() {
        editor.clear();
        editor.commit();
    }

    /*
    public String getId() {
        return preferencias.getString(CHAVE_ID, null);
    }

    public String getEmail() {
        return preferencias.getString(CHAVE_EMAIL, null);
    }
    */
}
