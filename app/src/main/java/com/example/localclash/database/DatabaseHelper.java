package com.example.localclash.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.localclash.models.Contacto;
import com.example.localclash.models.Cuenta;
import com.example.localclash.models.Deuda;
import com.example.localclash.models.Sucursal;
import com.example.localclash.models.Transaccion;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LocalCashDB.db";
    private static final int DATABASE_VERSION = 1;

    // Nombre de Tablas
    public static final String TABLE_CUENTAS = "Cuentas";
    public static final String TABLE_TRANSACCIONES = "Transacciones";
    public static final String TABLE_CONTACTOS = "Contactos";
    public static final String TABLE_DEUDAS = "Deudas";
    public static final String TABLE_SUCURSALES = "Sucursales";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear Tabla Cuentas
        db.execSQL("CREATE TABLE " + TABLE_CUENTAS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "tipo TEXT, " +
                "saldo REAL)");

        // Crear Tabla Transacciones
        db.execSQL("CREATE TABLE " + TABLE_TRANSACCIONES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "cuenta_id INTEGER, " +
                "tipo TEXT, " +
                "monto REAL, " +
                "descripcion TEXT, " +
                "fecha TEXT, " +
                "latitud REAL, " +
                "longitud REAL)");

        // Crear Tabla Contactos
        db.execSQL("CREATE TABLE " + TABLE_CONTACTOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "telefono TEXT, " +
                "email TEXT)");

        // Crear Tabla Deudas
        db.execSQL("CREATE TABLE " + TABLE_DEUDAS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "contacto_id INTEGER, " +
                "monto REAL, " +
                "descripcion TEXT, " +
                "estado TEXT, " +
                "fecha TEXT)");

        // Crear Tabla Sucursales
        db.execSQL("CREATE TABLE " + TABLE_SUCURSALES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "direccion TEXT, " +
                "latitud REAL, " +
                "longitud REAL)");

        // Cargar Datos Iniciales de Demostración
        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUENTAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACCIONES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEUDAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUCURSALES);
        onCreate(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        // Cuentas iniciales
        db.execSQL("INSERT INTO " + TABLE_CUENTAS + " (nombre, tipo, saldo) VALUES ('Efectivo', 'Efectivo', 50000.0)");
        db.execSQL("INSERT INTO " + TABLE_CUENTAS + " (nombre, tipo, saldo) VALUES ('Cuenta RUT', 'Débito', 120000.0)");

        // Contactos iniciales
        db.execSQL("INSERT INTO " + TABLE_CONTACTOS + " (nombre, telefono, email) VALUES ('Juan Pérez', '+56912345678', 'juan@gmail.com')");
        db.execSQL("INSERT INTO " + TABLE_CONTACTOS + " (nombre, telefono, email) VALUES ('María López', '+56987654321', 'maria@gmail.com')");

        // Deudas iniciales
        db.execSQL("INSERT INTO " + TABLE_DEUDAS + " (contacto_id, monto, descripcion, estado, fecha) VALUES (1, 15000.0, 'Almuerzo compartido', 'Pendiente', '2026-03-25')");
        db.execSQL("INSERT INTO " + TABLE_DEUDAS + " (contacto_id, monto, descripcion, estado, fecha) VALUES (2, 8500.0, 'Uber de regreso', 'Pendiente', '2026-03-28')");

        // Sucursales iniciales
        db.execSQL("INSERT INTO " + TABLE_SUCURSALES + " (nombre, direccion, latitud, longitud) VALUES ('Sucursal Santiago Centro', 'Av. Libertador Bernardo O''Higgins 1111', -33.4429, -70.6539)");
        db.execSQL("INSERT INTO " + TABLE_SUCURSALES + " (nombre, direccion, latitud, longitud) VALUES ('Sucursal La Florida', 'Av. Vicuña Mackenna 7110', -33.5227, -70.5983)");
        db.execSQL("INSERT INTO " + TABLE_SUCURSALES + " (nombre, direccion, latitud, longitud) VALUES ('Sucursal Providencia', 'Av. Providencia 1950', -33.4255, -70.6148)");
    }

    // --- MÉTODOS CRUD CUENTAS ---

    public long insertCuenta(Cuenta cuenta) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", cuenta.getNombre());
        values.put("tipo", cuenta.getTipo());
        values.put("saldo", cuenta.getSaldo());
        return db.insert(TABLE_CUENTAS, null, values);
    }

    public List<Cuenta> getAllCuentas() {
        List<Cuenta> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CUENTAS, null);

        if (cursor.moveToFirst()) {
            do {
                Cuenta c = new Cuenta(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("saldo"))
                );
                lista.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // --- MÉTODOS CRUD TRANSACCIONES ---

    public long insertTransaccion(Transaccion t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cuenta_id", t.getCuentaId());
        values.put("tipo", t.getTipo());
        values.put("monto", t.getMonto());
        values.put("descripcion", t.getDescripcion());
        values.put("fecha", t.getFecha());
        values.put("latitud", t.getLatitud());
        values.put("longitud", t.getLongitud());
        return db.insert(TABLE_TRANSACCIONES, null, values);
    }

    public List<Transaccion> getAllTransacciones() {
        List<Transaccion> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSACCIONES + " ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Transaccion t = new Transaccion(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("cuenta_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
                        cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                        cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"))
                );
                lista.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // --- MÉTODOS CRUD CONTACTOS ---

    public long insertContacto(Contacto c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", c.getNombre());
        values.put("telefono", c.getTelefono());
        values.put("email", c.getEmail());
        return db.insert(TABLE_CONTACTOS, null, values);
    }

    public List<Contacto> getAllContactos() {
        List<Contacto> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTOS, null);

        if (cursor.moveToFirst()) {
            do {
                Contacto c = new Contacto(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email"))
                );
                lista.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // --- MÉTODOS CRUD DEUDAS ---

    public long insertDeuda(Deuda d) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("contacto_id", d.getContactoId());
        values.put("monto", d.getMonto());
        values.put("descripcion", d.getDescripcion());
        values.put("estado", d.getEstado());
        values.put("fecha", d.getFecha());
        return db.insert(TABLE_DEUDAS, null, values);
    }

    public List<Deuda> getAllDeudas() {
        List<Deuda> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT d.id, d.contacto_id, c.nombre AS contacto_nombre, d.monto, d.descripcion, d.estado, d.fecha " +
                "FROM " + TABLE_DEUDAS + " d " +
                "LEFT JOIN " + TABLE_CONTACTOS + " c ON d.contacto_id = c.id";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Deuda d = new Deuda(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("contacto_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("contacto_nombre")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
                        cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                        cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                        cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
                );
                lista.add(d);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // --- MÉTODOS CRUD SUCURSALES ---

    public List<Sucursal> getAllSucursales() {
        List<Sucursal> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUCURSALES, null);

        if (cursor.moveToFirst()) {
            do {
                Sucursal s = new Sucursal(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"))
                );
                lista.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}