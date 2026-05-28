package com.ejemplo.model;

public class Movimiento {
    private int id;
    private int idPartida;
    private int orden;
    private String movimiento;

    public Movimiento() {}

    public Movimiento(int id, int idPartida, int orden, String movimiento) {
        this.id = id;
        this.idPartida = idPartida;
        this.orden = orden;
        this.movimiento = movimiento;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdPartida() { return idPartida; }
    public void setIdPartida(int idPartida) { this.idPartida = idPartida; }
    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
    public String getMovimiento() { return movimiento; }
    public void setMovimiento(String movimiento) { this.movimiento = movimiento; }
}
