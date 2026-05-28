package com.ejemplo.model;

import java.sql.Timestamp;

public class Partida {
    private int id;
    private String aliasBlancas;
    private String aliasNegras;
    private Timestamp fechaHoraInicio;
    private String resultado;

    public Partida() {}

    public Partida(int id, String aliasBlancas, String aliasNegras, Timestamp fechaHoraInicio, String resultado) {
        this.id = id;
        this.aliasBlancas = aliasBlancas;
        this.aliasNegras = aliasNegras;
        this.fechaHoraInicio = fechaHoraInicio;
        this.resultado = resultado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAliasBlancas() { return aliasBlancas; }
    public void setAliasBlancas(String aliasBlancas) { this.aliasBlancas = aliasBlancas; }
    public String getAliasNegras() { return aliasNegras; }
    public void setAliasNegras(String aliasNegras) { this.aliasNegras = aliasNegras; }
    public Timestamp getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(Timestamp fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
}
