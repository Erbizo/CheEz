package com.ejemplo.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SolicitudDAO {

    public void insertar(Solicitud solicitud) throws SQLException {
        String sql = "INSERT INTO solicitudes (alias_emisor, alias_receptor, estado) VALUES (?, ?, 'pendiente')";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, solicitud.getAliasEmisor());
            ps.setString(2, solicitud.getAliasReceptor());
            ps.executeUpdate();
        }
    }

    public Solicitud buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, alias_emisor, alias_receptor, estado, fecha_solicitud FROM solicitudes WHERE id = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Solicitud(rs.getInt("id"), rs.getString("alias_emisor"),
                        rs.getString("alias_receptor"), rs.getString("estado"),
                        rs.getTimestamp("fecha_solicitud"));
            }
        }
        return null;
    }

    public List<Solicitud> listarPendientesPorReceptor(String aliasReceptor) throws SQLException {
        List<Solicitud> lista = new ArrayList<>();
        String sql = "SELECT id, alias_emisor, alias_receptor, estado, fecha_solicitud FROM solicitudes WHERE alias_receptor = ? AND estado = 'pendiente'";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, aliasReceptor);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Solicitud(rs.getInt("id"), rs.getString("alias_emisor"),
                        rs.getString("alias_receptor"), rs.getString("estado"),
                        rs.getTimestamp("fecha_solicitud")));
            }
        }
        return lista;
    }

    public void actualizar(int id, String estado) throws SQLException {
        String sql = "UPDATE solicitudes SET estado = ? WHERE id = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void rechazarExpiradas() throws SQLException {
        String sql = "UPDATE solicitudes SET estado = 'rechazada' WHERE estado = 'pendiente' AND fecha_solicitud < DATE_SUB(NOW(), INTERVAL 5 MINUTE)";
        try (Connection con = ConexionBD.getConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate(sql);
        }
    }
}
