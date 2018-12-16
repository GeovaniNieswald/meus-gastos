package com.geovaninieswald.meusgastos.model.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.geovaninieswald.meusgastos.enumeration.TipoCategoria;
import com.geovaninieswald.meusgastos.helper.Utils;
import com.geovaninieswald.meusgastos.model.Categoria;
import com.geovaninieswald.meusgastos.model.Transacao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class TransacaoDAO {

    private GatewayDB gatewayDB;

    public TransacaoDAO(Context context) {
        gatewayDB = GatewayDB.getInstance(context);
    }

    public List<Long> salvar(Transacao transacao) {
        List<Long> result = new ArrayList<>();

        ContentValues cv;

        int quantidade = transacao.getQuantidade();
        Date data = transacao.getDataBD();
        int count = 0;

        ArrayList<Date> datas = new ArrayList<>();
        datas.add(data);

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(data);

        for (int i = 1; i < quantidade; i++) {
            if (gc.get(Calendar.MONTH) == Calendar.DECEMBER) {
                gc.roll(Calendar.MONTH, true);
                gc.roll(Calendar.YEAR, true);
            } else {
                gc.roll(Calendar.MONTH, true);
            }

            data = gc.getTime();
            datas.add(data);
        }

        while (count < quantidade) {
            String descricaoStr = transacao.getDescricao();
            String idCategoriaStr = transacao.getCategoria().getId() + "";
            String valorStr = transacao.getValorBD().toString();
            String dataStr = Utils.dateParaStringBD(datas.get(count));

            cv = new ContentValues();
            cv.put("descricao", descricaoStr);
            cv.put("id_categoria", idCategoriaStr);
            cv.put("valor", transacao.getValorBD().doubleValue());
            cv.put("data", dataStr);
            cv.put("paga", transacao.isPago() ? 1 : 0);

            if (transacaoExiste(descricaoStr, idCategoriaStr, valorStr, dataStr)) {
                result.add((long) -2);
            } else {
                result.add(gatewayDB.getDatabase().insert("transacao", null, cv));
            }

            count++;
        }

        return result;

    }

    public long alterar(Transacao transacao) {
        ContentValues cv = new ContentValues();
        cv.put("descricao", transacao.getDescricao());
        cv.put("id_categoria", transacao.getCategoria().getId());
        cv.put("valor", transacao.getValorBD().doubleValue());
        cv.put("data", Utils.dateParaStringBD(transacao.getDataBD()));
        cv.put("paga", transacao.isPago() ? 1 : 0);

        return gatewayDB.getDatabase().update("transacao", cv, "id = ?", new String[]{transacao.getId() + ""});
    }

    public int excluir(long id) {
        return gatewayDB.getDatabase().delete("transacao", "id = ?", new String[]{id + ""});
    }

    public boolean transacaoExiste(String descricaoStr, String idCategoriaStr, String valorStr, String dataStr) {
        Cursor cursor = gatewayDB.getDatabase().rawQuery("SELECT * FROM transacao WHERE descricao = ? AND id_categoria = ? AND valor = ? AND data = ?",
                new String[]{descricaoStr, idCategoriaStr, valorStr, dataStr});

        cursor.moveToFirst();
        int count = cursor.getCount();
        cursor.close();

        if (count > 0)
            return true;

        return false;
    }

    public List<Transacao> retornarTodas() throws ParseException {
        List<Transacao> transacoes = new ArrayList<>();

        Cursor cursor = gatewayDB.getDatabase().rawQuery("SELECT * FROM transacao AS t1 INNER JOIN categoria AS t2 ON (t1.id_categoria = t2.id) ORDER BY data", null);

        while (cursor.moveToNext()) {
            Transacao t = new Transacao();
            t.setId(cursor.getInt(0));
            t.setDescricao(cursor.getString(1));
            t.setValorBD(BigDecimal.valueOf(cursor.getDouble(2)));
            t.setDataBD(Utils.stringParaDateBD(cursor.getString(3)));

            if (cursor.getInt(4) == 1) {
                t.setPago(true);
            } else {
                t.setPago(false);
            }

            TipoCategoria tc = TipoCategoria.RENDIMENTO;

            if (cursor.getInt(8) == TipoCategoria.GASTO.getCodigo())
                tc = TipoCategoria.GASTO;

            Categoria c = new Categoria(cursor.getInt(5), tc, cursor.getString(7));

            t.setCategoria(c);

            transacoes.add(t);
        }

        cursor.close();

        return transacoes;
    }

    public List<Transacao> retornarPorMesAno(Date mesAno) throws ParseException {
        List<Transacao> transacoes = new ArrayList<>();

        String mesAnoStr = new SimpleDateFormat("yyyy/MM/").format(mesAno) + "%";

        Cursor cursor = gatewayDB.getDatabase().rawQuery("SELECT * FROM transacao AS t1 INNER JOIN categoria AS t2 ON (t1.id_categoria = t2.id) WHERE data LIKE ? ORDER BY data", new String[]{mesAnoStr});

        while (cursor.moveToNext()) {
            Transacao t = new Transacao();
            t.setId(cursor.getInt(0));
            t.setDescricao(cursor.getString(1));
            t.setValorBD(BigDecimal.valueOf(cursor.getDouble(2)));
            t.setDataBD(Utils.stringParaDateBD(cursor.getString(3)));

            if (cursor.getInt(4) == 1) {
                t.setPago(true);
            } else {
                t.setPago(false);
            }

            TipoCategoria tc = TipoCategoria.RENDIMENTO;

            if (cursor.getInt(8) == TipoCategoria.GASTO.getCodigo())
                tc = TipoCategoria.GASTO;

            Categoria c = new Categoria(cursor.getInt(5), tc, cursor.getString(7));

            t.setCategoria(c);

            transacoes.add(t);
        }

        cursor.close();

        return transacoes;
    }
}
