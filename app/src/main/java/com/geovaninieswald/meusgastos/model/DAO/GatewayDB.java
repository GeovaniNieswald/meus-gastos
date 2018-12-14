package com.geovaninieswald.meusgastos.model.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class GatewayDB {

    private static GatewayDB gatewayDB;
    private SQLiteDatabase banco;
    private ConexaoDB conexaoDB;

    private GatewayDB(Context context) {
        conexaoDB = new ConexaoDB(context);
        banco = conexaoDB.getWritableDatabase();
    }

    protected static GatewayDB getInstance(Context context) {
        if (gatewayDB == null)
            gatewayDB = new GatewayDB(context);

        return gatewayDB;
    }

    protected SQLiteDatabase getDatabase() {
        return this.banco;
    }

    protected void sair() {
        gatewayDB = null;
    }
}
