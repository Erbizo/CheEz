package com.ejemplo.controller;

import com.ejemplo.model.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/juego")
public class JuegoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("alias") == null) {
            response.setStatus(401);
            out.print("{\"resultado\":\"invalido\"}");
            return;
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }

        JsonObject body = JsonParser.parseString(sb.toString()).getAsJsonObject();
        int idPartida = body.get("idPartida").getAsInt();
        String origen = body.get("origen").getAsString();
        String destino = body.get("destino").getAsString();
        String aliasActual = (String) session.getAttribute("alias");

        try {
            PartidaDAO pDAO = new PartidaDAO();
            Partida partida = pDAO.buscarPorId(idPartida);

            if (partida == null || !"en_curso".equals(partida.getResultado())) {
                out.print("{\"resultado\":\"invalido\"}");
                return;
            }

            MovimientoDAO mDAO = new MovimientoDAO();
            List<Movimiento> historial = mDAO.listarPorPartida(idPartida);
            int totalMovimientos = historial.size();

            boolean esTurnoBlancas = totalMovimientos % 2 == 0;
            boolean esBlancas = aliasActual.equals(partida.getAliasBlancas());
            boolean esNegras = aliasActual.equals(partida.getAliasNegras());

            if ((esTurnoBlancas && !esBlancas) || (!esTurnoBlancas && !esNegras)) {
                out.print("{\"resultado\":\"invalido\"}");
                return;
            }

            ValidadorMovimiento validador = new ValidadorMovimiento(historial);
            if (validador.validar(origen, destino, esTurnoBlancas)) {
                Movimiento mov = new Movimiento();
                mov.setIdPartida(idPartida);
                mov.setOrden(totalMovimientos + 1);
                mov.setMovimiento(origen + "-" + destino);
                mDAO.insertar(mov);


                List<Movimiento> historialActualizado = mDAO.listarPorPartida(idPartida);
                ValidadorMovimiento posFinal = new ValidadorMovimiento(historialActualizado);

                boolean rivalEsBlancas = !esTurnoBlancas;


                if (posFinal.reyFueCapturado(rivalEsBlancas)) {
                    String ganador = esTurnoBlancas ? "blancas" : "negras";
                    pDAO.actualizar(idPartida, ganador);
                    out.print("{\"resultado\":\"valido\",\"jaque\":false,\"jaqueMate\":false,\"ahogado\":false,\"reyCapturado\":true}");
                    return;
                }

                boolean jaque     = posFinal.estaEnJaque(rivalEsBlancas);
                boolean jaqueMate = jaque && posFinal.estaEnJaqueMate(rivalEsBlancas);
                boolean ahogado   = !jaque && posFinal.estaEnAhogado(rivalEsBlancas);

                if (jaqueMate) {
                    String ganador = esTurnoBlancas ? "blancas" : "negras";
                    pDAO.actualizar(idPartida, ganador);
                } else if (ahogado) {
                    pDAO.actualizar(idPartida, "tablas");
                }

                out.print("{\"resultado\":\"valido\",\"jaque\":" + jaque + ",\"jaqueMate\":" + jaqueMate + ",\"ahogado\":" + ahogado + ",\"reyCapturado\":false}");
            } else {
                out.print("{\"resultado\":\"invalido\"}");
            }

        } catch (SQLException e) {
            response.setStatus(500);
            out.print("{\"resultado\":\"invalido\"}");
        }
    }
}
