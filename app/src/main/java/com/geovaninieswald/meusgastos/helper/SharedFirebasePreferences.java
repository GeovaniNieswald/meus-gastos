package com.geovaninieswald.meusgastos.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedFirebasePreferences {

    private SharedPreferences preferencias;
    private final String NOME_ARQUIVO = "meus-gastos.preferencias";
    private final int MODO = Context.MODE_PRIVATE;
    private SharedPreferences.Editor editor;

    private final String CHAVE_ID = "id";

    public SharedFirebasePreferences(Context context) {
        preferencias = context.getSharedPreferences(NOME_ARQUIVO, MODO);
        editor = preferencias.edit();
    }

    public void salvarLogin(String id) {
        editor.putString(CHAVE_ID, id);
        editor.commit();
    }

    public boolean verificarLogin() {
        return preferencias.contains("id");
    }

    public void sair() {
        editor.clear();
        editor.commit();
    }
}
