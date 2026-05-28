package com.ejemplo.model;

import java.sql.*;

public class PartidaDAO {

    public int insertar(Partida partida) throws SQLException {
        String sql = "INSERT INTO partidas (alias_blancas, alias_negras, resultado) VALUES (?, ?, 'en_curso')";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, partida.getAliasBlancas());
            ps.setString(2, partida.getAliasNegras());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        }
    }

    public Partida buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, alias_blancas, alias_negras, fecha_hora_inicio, resultado FROM partidas WHERE id = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Partida(rs.getInt("id"), rs.getString("alias_blancas"),
                        rs.getString("alias_negras"), rs.getTimestamp("fecha_hora_inicio"),
                        rs.getString("resultado"));
            }
        }
        return null;
    }

    public void actualizar(int id, String resultado) throws SQLException {
        String sql = "UPDATE partidas SET resultado = ? WHERE id = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, resultado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public Partida buscarPartidaActiva(String alias) throws SQLException {
        String sql = "SELECT id, alias_blancas, alias_negras, fecha_hora_inicio, resultado FROM partidas WHERE resultado = 'en_curso' AND (alias_blancas = ? OR alias_negras = ?) LIMIT 1";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, alias);
            ps.setString(2, alias);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Partida(rs.getInt("id"), rs.getString("alias_blancas"),
                        rs.getString("alias_negras"), rs.getTimestamp("fecha_hora_inicio"),
                        rs.getString("resultado"));
            }
        }
        return null;
    }
}
