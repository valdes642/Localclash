package com.example.localclash.models;

public class Transaccion {
    private int id;
    private int cuentaId;
    private String tipo; // "Ingreso" o "Egreso"
    private double monto;
    private String descripcion;
    private String fecha;
    private double latitud;
    private double longitud;

    public Transaccion() {}

    public Transaccion(int id, int cuentaId, String tipo, double monto, String descripcion, String fecha, double latitud, double longitud) {
        this.id = id;
        this.cuentaId = cuentaId;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCuentaId() { return cuentaId; }
    public void setCuentaId(int cuentaId) { this.cuentaId = cuentaId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
}