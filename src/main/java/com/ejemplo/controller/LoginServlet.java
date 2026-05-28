package com.ejemplo.controller;

import com.ejemplo.model.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }

        JsonObject body = JsonParser.parseString(sb.toString()).getAsJsonObject();
        String alias = body.get("alias").getAsString().trim();

        if (alias.isEmpty() || alias.length() > 8) {
            response.setStatus(400);
            out.print("{\"ok\":false,\"mensaje\":\"Alias invalido\"}");
            return;
        }

        try {
            JugadorDAO dao = new JugadorDAO();
            if (!dao.existePorAlias(alias)) {
                Jugador jugador = new Jugador();
                jugador.setAlias(alias);
                dao.insertar(jugador);
            }
            HttpSession session = request.getSession(true);
            session.setAttribute("alias", alias);
            out.print("{\"ok\":true}");
        } catch (SQLException e) {
            response.setStatus(500);
            out.print("{\"ok\":false,\"mensaje\":\"Error de base de datos\"}");
        }
    }
}
