package com.example.localclash.models;

public class Cuenta {
    private int id;
    private String nombre;
    private String tipo;
    private double saldo;

    public Cuenta() {}

    public Cuenta(int id, String nombre, String tipo, double saldo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.saldo = saldo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
}