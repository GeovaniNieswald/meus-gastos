package com.geovaninieswald.meusgastos.model.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.geovaninieswald.meusgastos.model.Categoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    private GatewayDB gatewayDB;

    public CategoriaDAO(Context context) {
        gatewayDB = GatewayDB.getInstance(context);
    }

    public long salvar(Categoria categoria) {
        if (categoriaExiste(categoria.getDescricao(), categoria.getTipoCategoria())) {
            return -2;
        } else {
            ContentValues cv = new ContentValues();
            cv.put("descricao", categoria.getDescricao());
            cv.put("tipo", categoria.getTipoCategoria().getCodigo());

            return gatewayDB.getDatabase().insert("categoria", null, cv);
        }
    }

    public int excluir(long id) {
        try {
            return gatewayDB.getDatabase().delete("categoria", "id = ?", new String[]{id + ""});
        } catch (SQLiteConstraintException e) {
            return 0;
        }
    }

    public boolean categoriaExiste(String descricao, TipoCategoria tipo) {
        Cursor cursor = gatewayDB.getDatabase().rawQuery("SELECT * FROM categoria WHERE descricao = ? AND tipo = ?", new String[]{descricao, tipo.getCodigo() + ""});
        cursor.moveToFirst();
        int count = cursor.getCount();
        cursor.close();

        if (count > 0)
            return true;

        return false;
    }

    public List<Categoria> retornarTodas() {
        List<Categoria> categorias = new ArrayList<>();

        Cursor cursor = gatewayDB.getDatabase().rawQuery("SELECT * FROM categoria ORDER BY descricao", null);

        while (cursor.moveToNext()) {
            Categoria c = new Categoria();
            c.setId(cursor.getInt(0));
            c.setDescricao(cursor.getString(1));

            if (cursor.getInt(2) == 0) {
                c.setTipoCategoria(TipoCategoria.GASTO);
            } else if (cursor.getInt(2) == 1) {
                c.setTipoCategoria(TipoCategoria.RENDIMENTO);
            }

            categorias.add(c);
        }

        cursor.close();

        return categorias;
    }

    public List<Categoria> retornarPorTipo(TipoCategoria tc) {
        List<Categoria> categorias = new ArrayList<>();

        Cursor cursor = gatewayDB.getDatabase().rawQuery("SELECT * FROM categoria WHERE tipo = ? ORDER BY descricao", new String[]{tc.getCodigo() + ""});

        while (cursor.moveToNext()) {
            Categoria c = new Categoria();
            c.setId(cursor.getInt(0));
            c.setDescricao(cursor.getString(1));
            c.setTipoCategoria(tc);

            categorias.add(c);
        }

        cursor.close();

        return categorias;
    }
}
