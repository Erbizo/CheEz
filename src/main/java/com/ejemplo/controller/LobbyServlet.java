package com.ejemplo.controller;

import com.ejemplo.model.*;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/lobby")
public class LobbyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("alias") == null) {
            response.setStatus(401);
            out.print("{\"ok\":false,\"mensaje\":\"No autenticado\"}");
            return;
        }

        String aliasActual = (String) session.getAttribute("alias");

        try {
            new SolicitudDAO().rechazarExpiradas();
            List<Jugador> todos = new JugadorDAO().listarTodos();
            List<Jugador> otros = todos.stream()
                    .filter(j -> !j.getAlias().equals(aliasActual))
                    .collect(Collectors.toList());
            out.print(new Gson().toJson(otros));
        } catch (SQLException e) {
            response.setStatus(500);
            out.print("{\"ok\":false,\"mensaje\":\"Error de base de datos\"}");
        }
    }
}
