package com.geovaninieswald.meusgastos.model.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.geovaninieswald.meusgastos.model.Categoria;
import com.geovaninieswald.meusgastos.model.Rendimento;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class RendimentoDAO {

    private GatewayDB gatewayDB;

    public RendimentoDAO(Context context) {
        gatewayDB = GatewayDB.getInstance(context);
    }

    public long salvar(Rendimento rendimento) {
        if (rendimentoExiste(rendimento)) {
            return -2;
        } else {
            ContentValues cv;

            int quantidade = rendimento.getQuantidade();
            Date data = rendimento.getData();
            int count = 0;
            long result = 0;

            ArrayList<Date> datas = new ArrayList<>();
            GregorianCalendar gc = new GregorianCalendar();

            for (int i = 0; i < quantidade; i++) {
                gc.setTime(data);
                gc.roll(GregorianCalendar.MONTH, i);

                datas.add(gc.getTime());
            }

            while (count < quantidade && result != -1) {
                cv = new ContentValues();
                cv.put("descricao", rendimento.getDescricao());
                cv.put("id_categoria", rendimento.getCategoria().getId());
                cv.put("valor", rendimento.getValor().doubleValue());
                cv.put("data", new SimpleDateFormat("dd/MM/yyyy").format(datas.get(count)));

                result = gatewayDB.getDatabase().insert("rendimento", null, cv);
                count++;
            }

            return result;
        }
    }

    public boolean rendimentoExiste(Rendimento rendimento) {
        Cursor cursor = gatewayDB.getDatabase().rawQuery("SELECT * FROM rendimento WHERE descricao = ? AND id_categoria = ? AND valor = ? AND data = ?",
                new String[]{rendimento.getDescricao(), rendimento.getCategoria().getId() + "", rendimento.getValor().toString(), new SimpleDateFormat("dd/MM/yyyy").format(rendimento.getData())});

        cursor.moveToFirst();
        int count = cursor.getCount();
        cursor.close();

        if (count > 0)
            return true;

        return false;
    }

    public List<Rendimento> retornarTodos() throws ParseException {
        List<Rendimento> rendimentos = new ArrayList<>();

        Cursor cursor = gatewayDB.getDatabase().rawQuery("SELECT * FROM rendimento AS t1 INNER JOIN categoria AS t2 ON (t1.id_categoria = t2.id)", null);

        while (cursor.moveToNext()) {
            Rendimento r = new Rendimento();
            r.setId(cursor.getInt(0));
            r.setDescricao(cursor.getString(1));
            r.setValor(BigDecimal.valueOf(cursor.getDouble(2)));
            r.setData(new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(3)));

            Categoria c = new Categoria(cursor.getInt(4), TipoCategoria.RENDIMENTO, cursor.getString(6));

            r.setCategoria(c);

            rendimentos.add(r);
        }

        cursor.close();

        return rendimentos;
    }
}
