package com.geovaninieswald.meusgastos.model.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.geovaninieswald.meusgastos.model.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

public class UsuarioDAO {

    private DatabaseReference referencia;
    private GatewayDB gatewayDB;

    public UsuarioDAO(Context context) {
        referencia = ConexaoFirebase.getFirebase("usuarios");
        gatewayDB = GatewayDB.getInstance(context);
    }

    public boolean salvar(Usuario usuario) {
        boolean firebaseOk;

        Task<Void> task = referencia.child(usuario.getId()).setValue(usuario);

        if (task.isSuccessful()) {
            firebaseOk = true;
        } else {
            firebaseOk = false;
        }

        if (usuarioExiste(usuario.getId())) {
            alterar(usuario);
        } else {
            ContentValues cv = new ContentValues();
            cv.put("id", usuario.getId());
            cv.put("nome", usuario.getNome());
            cv.put("imagem", usuario.getImagem());
            cv.put("email", usuario.getEmail());

            gatewayDB.getDatabase().insert("usuario", null, cv);
        }

        return firebaseOk;
    }

    public void alterar(Usuario usuario) {
        ContentValues cv = new ContentValues();
        cv.put("id", usuario.getId());
        cv.put("nome", usuario.getNome());
        cv.put("imagem", usuario.getImagem());
        cv.put("email", usuario.getEmail());

        gatewayDB.getDatabase().update("usuario", cv, "id = ?", new String[]{usuario.getId()});
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
}
