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
import java.util.List;

@WebServlet("/solicitud")
public class SolicitudServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("alias") == null) {
            response.setStatus(401);
            out.print("[]");
            return;
        }

        try {
            List<Solicitud> lista = new SolicitudDAO()
                    .listarPendientesPorReceptor((String) session.getAttribute("alias"));
            out.print(new Gson().toJson(lista));
        } catch (SQLException e) {
            response.setStatus(500);
            out.print("[]");
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
        String accion = body.get("accion").getAsString();
        String aliasActual = (String) session.getAttribute("alias");

        try {
            SolicitudDAO solDAO = new SolicitudDAO();
            if ("enviar".equals(accion)) {
                Solicitud sol = new Solicitud();
                sol.setAliasEmisor(aliasActual);
                sol.setAliasReceptor(body.get("aliasReceptor").getAsString());
                solDAO.insertar(sol);
                out.print("{\"ok\":true}");

            } else if ("responder".equals(accion)) {
                int idSolicitud = body.get("idSolicitud").getAsInt();
                String respuesta = body.get("respuesta").getAsString();
                solDAO.actualizar(idSolicitud, respuesta);

                if ("aceptada".equals(respuesta)) {
                    Solicitud sol = solDAO.buscarPorId(idSolicitud);
                    Partida partida = new Partida();
                    partida.setAliasBlancas(sol.getAliasEmisor());
                    partida.setAliasNegras(sol.getAliasReceptor());
                    int idPartida = new PartidaDAO().insertar(partida);
                    out.print("{\"ok\":true,\"idPartida\":" + idPartida + "}");
                } else {
                    out.print("{\"ok\":true}");
                }
            } else {
                response.setStatus(400);
                out.print("{\"ok\":false}");
            }
        } catch (SQLException e) {
            response.setStatus(500);
            out.print("{\"ok\":false}");
        }
    }
}
