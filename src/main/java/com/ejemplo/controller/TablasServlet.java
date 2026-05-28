package com.ejemplo.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/tablas")
public class TablasServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    private ConcurrentHashMap<Integer, String> getOfertasMap() {
        ConcurrentHashMap<Integer, String> map =
            (ConcurrentHashMap<Integer, String>) getServletContext().getAttribute("tablas_ofertas");
        if (map == null) {
            map = new ConcurrentHashMap<>();
            getServletContext().setAttribute("tablas_ofertas", map);
        }
        return map;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        if (idParam == null) { response.setStatus(400); out.print("{\"ok\":false}"); return; }

        int idPartida = Integer.parseInt(idParam);
        String ofertante = getOfertasMap().get(idPartida);

        if (ofertante != null) {
            out.print("{\"ofertante\":\"" + ofertante + "\"}");
        } else {
            out.print("{\"ofertante\":null}");
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
            response.setStatus(401); out.print("{\"ok\":false}"); return;
        }
        String alias = (String) session.getAttribute("alias");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }

        JsonObject body = JsonParser.parseString(sb.toString()).getAsJsonObject();
        int idPartida = body.get("idPartida").getAsInt();
        String accion = body.get("accion").getAsString();

        ConcurrentHashMap<Integer, String> ofertas = getOfertasMap();
        if ("ofrecer".equals(accion)) {
            ofertas.put(idPartida, alias);
        } else {
            ofertas.remove(idPartida);
        }
        out.print("{\"ok\":true}");
    }
}
