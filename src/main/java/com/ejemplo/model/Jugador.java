package com.ejemplo.model;

import java.sql.Timestamp;

public class Jugador {
    private String alias;
    private Timestamp fechaIngreso;

    public Jugador() {}

    public Jugador(String alias, Timestamp fechaIngreso) {
        this.alias = alias;
        this.fechaIngreso = fechaIngreso;
    }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public Timestamp getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Timestamp fechaIngreso) { this.fechaIngreso = fechaIngreso; }
}
