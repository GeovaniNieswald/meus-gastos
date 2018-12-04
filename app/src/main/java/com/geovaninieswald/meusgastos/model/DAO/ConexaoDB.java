package com.geovaninieswald.meusgastos.model.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConexaoDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "meus_gastos.db";
    private static final int DATABASE_VERSION = 1;

    private final String CREATE_TABLE_USUARIO = "CREATE TABLE IF NOT EXISTS usuario (id TEXT PRIMARY KEY, nome TEXT NOT NULL, imagem TEXT, email TEXT NOT NULL);";
    private final String CREATE_TABLE_CATEGORIA = "CREATE TABLE IF NOT EXISTS categoria (id INTEGER PRIMARY KEY AUTOINCREMENT, descricao TEXT NOT NULL, tipo INT NOT NULL);";
    //private final String CREATE_TABLE_DESPESA = "CREATE TABLE IF NOT EXISTS despesa ();";
    //private final String CREATE_TABLE_RENDIMENTO = "CREATE TABLE IF NOT EXISTS rendimento ();";

    private final String DROP_TABLE_USUARIO = "DROP TABLE IF EXISTS usuario";
    private final String DROP_TABLE_CATEGORIA = "DROP TABLE IF EXISTS categoria";
    //private final String DROP_TABLE_DESPESA = "DROP TABLE IF EXISTS despesa";
    //private final String DROP_TABLE_RENDIMENTO = "DROP TABLE IF EXISTS rendimento";

    protected ConexaoDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USUARIO);
        db.execSQL(CREATE_TABLE_CATEGORIA);
        //db.execSQL(CREATE_TABLE_DESPESA);
        //db.execSQL(CREATE_TABLE_RENDIMENTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_USUARIO);
        db.execSQL(DROP_TABLE_CATEGORIA);
        //db.execSQL(DROP_TABLE_DESPESA);
        //db.execSQL(DROP_TABLE_RENDIMENTO);
        onCreate(db);
    }
}
