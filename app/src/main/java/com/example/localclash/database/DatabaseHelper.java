package com.example.localclash.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LocalCashDB";
    private static final int DATABASE_VERSION = 1;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Cuentas (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, saldo REAL)");
        db.execSQL("CREATE TABLE Contactos (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, telefono TEXT)");
        db.execSQL("CREATE TABLE Transacciones (id INTEGER PRIMARY KEY AUTOINCREMENT, cuenta_id INTEGER, monto REAL, latitud REAL, longitud REAL)");
        db.execSQL("CREATE TABLE Deudas (id INTEGER PRIMARY KEY AUTOINCREMENT, contacto_id INTEGER, monto REAL, descripcion TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Cuentas");
        db.execSQL("DROP TABLE IF EXISTS Contactos");
        db.execSQL("DROP TABLE IF EXISTS Transacciones");
        db.execSQL("DROP TABLE IF EXISTS Deudas");
        onCreate(db);
    }
}



