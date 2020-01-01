package com.danito.p_agendaavanzada;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BDContactos extends SQLiteOpenHelper {
    private String statement = "CREATE TABLE IF NOT EXISTS contactos(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombre TEXT," +
            "apellido TEXT," +
            "telefono TEXT," +
            "correo TEXT," +
            "imagen BLOB," +
            "amigo INTEGER," +
            "familia INTEGER," +
            "trabajo INTEGER" +
            ")";

    public BDContactos(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(statement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
