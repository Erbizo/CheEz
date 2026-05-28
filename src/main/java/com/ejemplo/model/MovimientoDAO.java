package com.ejemplo.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoDAO {

    public void insertar(Movimiento movimiento) throws SQLException {
        String sql = "INSERT INTO movimientos (id_partida, orden, movimiento) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, movimiento.getIdPartida());
            ps.setInt(2, movimiento.getOrden());
            ps.setString(3, movimiento.getMovimiento());
            ps.executeUpdate();
        }
    }

    public List<Movimiento> listarPorPartida(int idPartida) throws SQLException {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT id, id_partida, orden, movimiento FROM movimientos WHERE id_partida = ? ORDER BY orden ASC";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPartida);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Movimiento(rs.getInt("id"), rs.getInt("id_partida"),
                        rs.getInt("orden"), rs.getString("movimiento")));
            }
        }
        return lista;
    }

}
