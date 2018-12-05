package com.geovaninieswald.meusgastos.model.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.geovaninieswald.meusgastos.model.Categoria;
import com.geovaninieswald.meusgastos.model.Transacao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class TransacaoDAO {

    private GatewayDB gatewayDB;

    public TransacaoDAO(Context context) {
        gatewayDB = GatewayDB.getInstance(context);
    }

    public long salvar(Transacao transacao) {
        if (transacaoExiste(transacao)) {
            return -2;
        } else {
            ContentValues cv;

            int quantidade = transacao.getQuantidade();
            Date data = transacao.getData();
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
                cv.put("descricao", transacao.getDescricao());
                cv.put("id_categoria", transacao.getCategoria().getId());
                cv.put("valor", transacao.getValor().doubleValue());
                cv.put("data", new SimpleDateFormat("dd/MM/yyyy").format(datas.get(count)));
                cv.put("paga", transacao.isPaga() ? 1 : 0);

                result = gatewayDB.getDatabase().insert("transacao", null, cv);
                count++;
            }

            return result;
        }
    }

    public boolean transacaoExiste(Transacao transacao) {
        Cursor cursor = gatewayDB.getDatabase().rawQuery("SELECT * FROM transacao WHERE descricao = ? AND id_categoria = ? AND valor = ? AND data = ? AND paga = ?",
                new String[]{transacao.getDescricao(), transacao.getCategoria().getId() + "", transacao.getValor().toString(), new SimpleDateFormat("dd/MM/yyyy").format(transacao.getData()), (transacao.isPaga() ? 1 : 0) + ""});

        cursor.moveToFirst();
        int count = cursor.getCount();
        cursor.close();

        if (count > 0)
            return true;

        return false;
    }

    public List<Transacao> retornarTodas() throws ParseException {
        List<Transacao> transacoes = new ArrayList<>();

        Cursor cursor = gatewayDB.getDatabase().rawQuery("SELECT * FROM transacao AS t1 INNER JOIN categoria AS t2 ON (t1.id_categoria = t2.id)", null);

        while (cursor.moveToNext()) {
            Transacao t = new Transacao();
            t.setId(cursor.getInt(0));
            t.setDescricao(cursor.getString(1));
            t.setValor(BigDecimal.valueOf(cursor.getDouble(2)));
            t.setData(new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(3)));

            if(cursor.getInt(4) == 1){
                t.setPaga(true);
            } else {
                t.setPaga(false);
            }

            Categoria c = new Categoria(cursor.getInt(5), TipoCategoria.RENDIMENTO, cursor.getString(7));

            t.setCategoria(c);

            transacoes.add(t);
        }

        cursor.close();

        return transacoes;
    }
}
