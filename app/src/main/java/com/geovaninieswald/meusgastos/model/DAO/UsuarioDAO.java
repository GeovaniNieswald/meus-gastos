package com.geovaninieswald.meusgastos.model.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.geovaninieswald.meusgastos.model.Usuario;

public class UsuarioDAO {

    private GatewayDB gatewayDB;

    public UsuarioDAO(Context context) {
        gatewayDB = GatewayDB.getInstance(context);
    }

    public long salvar(Usuario usuario) {
        if (usuarioExiste(usuario.getId())) {
            return alterar(usuario);
        } else {
            ContentValues cv = new ContentValues();
            cv.put("id", usuario.getId());
            cv.put("nome", usuario.getNome());
            cv.put("imagem", usuario.getImagem());
            cv.put("email", usuario.getEmail());

            return gatewayDB.getDatabase().insert("usuario", null, cv);
        }
    }

    public long alterar(Usuario usuario) {
        ContentValues cv = new ContentValues();
        cv.put("id", usuario.getId());
        cv.put("nome", usuario.getNome());
        cv.put("imagem", usuario.getImagem());
        cv.put("email", usuario.getEmail());

        return gatewayDB.getDatabase().update("usuario", cv, "id = ?", new String[]{usuario.getId()});
    }

    public boolean usuarioExiste(String id) {
        Cursor cursor = gatewayDB.getDatabase().rawQuery("SELECT * FROM usuario WHERE id = ?", new String[]{id});
        cursor.moveToFirst();
        int count = cursor.getCount();
        cursor.close();

        if (count > 0)
            return true;

        return false;
    }

    public void sair(){
        gatewayDB.sair();
    }
}
