package com.ejemplo.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JugadorDAO {

    public boolean existePorAlias(String alias) throws SQLException {
        String sql = "SELECT COUNT(*) FROM jugadores WHERE alias = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, alias);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public void insertar(Jugador jugador) throws SQLException {
        String sql = "INSERT INTO jugadores (alias) VALUES (?)";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, jugador.getAlias());
            ps.executeUpdate();
        }
    }

    public List<Jugador> listarTodos() throws SQLException {
        List<Jugador> lista = new ArrayList<>();
        String sql = "SELECT alias, fecha_ingreso FROM jugadores ORDER BY fecha_ingreso DESC";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Jugador(rs.getString("alias"), rs.getTimestamp("fecha_ingreso")));
            }
        }
        return lista;
    }

    public void eliminar(String alias) throws SQLException {
        String sql = "DELETE FROM jugadores WHERE alias = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, alias);
            ps.executeUpdate();
        }
    }
}
