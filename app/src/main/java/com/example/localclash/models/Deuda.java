package com.example.localclash.models;

public class Deuda {
    private int id;
    private int contactoId;
    private String contactoNombre;
    private double monto;
    private String descripcion;
    private String estado; // "Pendiente" o "Pagado"
    private String fecha;

    public Deuda() {}

    public Deuda(int id, int contactoId, String contactoNombre, double monto, String descripcion, String estado, String fecha) {
        this.id = id;
        this.contactoId = contactoId;
        this.contactoNombre = contactoNombre;
        this.monto = monto;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getContactoId() { return contactoId; }
    public void setContactoId(int contactoId) { this.contactoId = contactoId; }

    public String getContactoNombre() { return contactoNombre; }
    public void setContactoNombre(String contactoNombre) { this.contactoNombre = contactoNombre; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}