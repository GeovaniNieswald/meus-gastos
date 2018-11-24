package com.geovaninieswald.meusgastos.model.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.geovaninieswald.meusgastos.model.Usuario;
import com.google.firebase.database.DatabaseReference;

public class UsuarioDAO {

    private DatabaseReference referencia;
    private GatewayDB gatewayDB;

    private final String TABLE_USUARIO = "usuario";

    public UsuarioDAO(Context context) {
        referencia = ConexaoFirebase.getFirebase("usuarios");
        gatewayDB = GatewayDB.getInstance(context);
    }

    public long salvar(Usuario usuario) {
        // pegar a task retornar status da operação
        referencia.child(usuario.getId()).setValue(usuario);

        if (usuarioExiste(usuario.getId()))
            return alterar(usuario);

        ContentValues cv = new ContentValues();
        cv.put("id", usuario.getId());
        cv.put("nome", usuario.getNome());
        cv.put("imagem", usuario.getImagem());
        cv.put("email", usuario.getEmail());

        return gatewayDB.getDatabase().insert(TABLE_USUARIO, null, cv);
    }

    public long alterar(Usuario usuario) {
        ContentValues cv = new ContentValues();
        cv.put("id", usuario.getId());
        cv.put("nome", usuario.getNome());
        cv.put("imagem", usuario.getImagem());
        cv.put("email", usuario.getEmail());

        return gatewayDB.getDatabase().update(TABLE_USUARIO, cv, "id = ?", new String[]{usuario.getId()});
    }

    public boolean usuarioExiste(String id) {
        try {
            Cursor cursor = gatewayDB.getDatabase().rawQuery("SELECT * FROM " + TABLE_USUARIO + " WHERE id = ?", new String[]{id});
            cursor.moveToFirst();
            int count = cursor.getCount();
            cursor.close();

            if (count > 0)
                return true;


        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return false;
    }

    /* Método que retorna um o usuário
    public Usuario buscarPorID(String id) {
        Usuario usuario;

        Cursor cursor = gatewayDB.getDatabase().rawQuery("SELECT * FROM TABLE_USUARIO WHERE id = ?", new String[]{id});
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            usuario = new Usuario();

            usuario.setId(cursor.getString(0));
            usuario.setNome(cursor.getString(1));
            usuario.setImagem(cursor.getString(2));
            usuario.setEmail(cursor.getString(3));
        } else {
            usuario = null;
        }

        cursor.close();

        return usuario;
    }
    */
}
