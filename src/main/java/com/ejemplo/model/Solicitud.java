package com.ejemplo.model;

import java.sql.Timestamp;

public class Solicitud {
    private int id;
    private String aliasEmisor;
    private String aliasReceptor;
    private String estado;
    private Timestamp fechaSolicitud;

    public Solicitud() {}

    public Solicitud(int id, String aliasEmisor, String aliasReceptor, String estado, Timestamp fechaSolicitud) {
        this.id = id;
        this.aliasEmisor = aliasEmisor;
        this.aliasReceptor = aliasReceptor;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAliasEmisor() { return aliasEmisor; }
    public void setAliasEmisor(String aliasEmisor) { this.aliasEmisor = aliasEmisor; }
    public String getAliasReceptor() { return aliasReceptor; }
    public void setAliasReceptor(String aliasReceptor) { this.aliasReceptor = aliasReceptor; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Timestamp getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Timestamp fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
}
