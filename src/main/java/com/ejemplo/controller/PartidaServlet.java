package com.ejemplo.controller;

import com.ejemplo.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/partida")
public class PartidaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String activaParam = request.getParameter("activa");
        if ("true".equals(activaParam)) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("alias") == null) {
                response.setStatus(401);
                out.print("{\"ok\":false}");
                return;
            }
            String alias = (String) session.getAttribute("alias");
            try {
                Partida partida = new PartidaDAO().buscarPartidaActiva(alias);
                if (partida != null) {
                    out.print("{\"idPartida\":" + partida.getId() + "}");
                } else {
                    out.print("{}");
                }
            } catch (SQLException e) {
                response.setStatus(500);
                out.print("{\"ok\":false}");
            }
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.setStatus(400);
            out.print("{\"ok\":false}");
            return;
        }

        try {
            int idPartida = Integer.parseInt(idParam);
            Partida partida = new PartidaDAO().buscarPorId(idPartida);
            List<Movimiento> movimientos = new MovimientoDAO().listarPorPartida(idPartida);
            Map<String, Object> result = new HashMap<>();
            result.put("partida", partida);
            result.put("movimientos", movimientos);
            out.print(new Gson().toJson(result));
        } catch (SQLException | NumberFormatException e) {
            response.setStatus(500);
            out.print("{\"ok\":false}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("alias") == null) {
            response.setStatus(401);
            out.print("{\"ok\":false}");
            return;
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }

        JsonObject body = JsonParser.parseString(sb.toString()).getAsJsonObject();
        int idPartida = body.get("idPartida").getAsInt();
        String resultado = body.get("resultado").getAsString();

        try {
            new PartidaDAO().actualizar(idPartida, resultado);
            out.print("{\"ok\":true}");
        } catch (SQLException e) {
            response.setStatus(500);
            out.print("{\"ok\":false}");
        }
    }
}
