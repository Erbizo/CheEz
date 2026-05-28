package com.ejemplo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidadorMovimiento {

    private final Map<String, String> tablero = new HashMap<>();

    private static final String[][] INICIO = {
        {"a8","negras_torre"},{"b8","negras_caballo"},{"c8","negras_alfil"},{"d8","negras_dama"},
        {"e8","negras_rey"},{"f8","negras_alfil"},{"g8","negras_caballo"},{"h8","negras_torre"},
        {"a7","negras_peon"},{"b7","negras_peon"},{"c7","negras_peon"},{"d7","negras_peon"},
        {"e7","negras_peon"},{"f7","negras_peon"},{"g7","negras_peon"},{"h7","negras_peon"},
        {"a2","blancas_peon"},{"b2","blancas_peon"},{"c2","blancas_peon"},{"d2","blancas_peon"},
        {"e2","blancas_peon"},{"f2","blancas_peon"},{"g2","blancas_peon"},{"h2","blancas_peon"},
        {"a1","blancas_torre"},{"b1","blancas_caballo"},{"c1","blancas_alfil"},{"d1","blancas_dama"},
        {"e1","blancas_rey"},{"f1","blancas_alfil"},{"g1","blancas_caballo"},{"h1","blancas_torre"}
    };

    public ValidadorMovimiento(List<Movimiento> historial) {
        for (String[] entrada : INICIO) tablero.put(entrada[0], entrada[1]);
        for (Movimiento m : historial) {
            String[] partes = m.getMovimiento().split("-");
            tablero.put(partes[1], tablero.remove(partes[0]));
        }
    }

    public boolean validar(String origen, String destino, boolean turnoBlancas) {
        if (origen == null || destino == null || origen.equals(destino)) return false;
        String pieza = tablero.get(origen);
        if (pieza == null) return false;
        boolean esBlanca = pieza.startsWith("blancas");
        if (esBlanca != turnoBlancas) return false;
        String ocupante = tablero.get(destino);
        if (ocupante != null && ocupante.startsWith(esBlanca ? "blancas" : "negras")) return false;
        String tipo = pieza.split("_")[1];
        switch (tipo) {
            case "peon":    return validarPeon(origen, destino, esBlanca);
            case "torre":   return validarTorre(origen, destino);
            case "alfil":   return validarAlfil(origen, destino);
            case "dama":    return validarTorre(origen, destino) || validarAlfil(origen, destino);
            case "caballo": return validarCaballo(origen, destino);
            case "rey":     return validarRey(origen, destino);
            default:        return false;
        }
    }

    private int col(String pos) { return pos.charAt(0) - 'a'; }
    private int fil(String pos) { return pos.charAt(1) - '1'; }
    private String casilla(int col, int fil) { return "" + (char)('a' + col) + (char)('1' + fil); }

    private boolean validarPeon(String o, String d, boolean esBlanca) {
        int dc = col(d) - col(o), df = fil(d) - fil(o);
        int dir = esBlanca ? 1 : -1;
        int filaInicial = esBlanca ? 1 : 6;
        if (dc == 0 && df == dir && tablero.get(d) == null) return true;
        if (dc == 0 && fil(o) == filaInicial && df == 2 * dir
                && tablero.get(d) == null
                && tablero.get(casilla(col(o), fil(o) + dir)) == null) return true;
        if (Math.abs(dc) == 1 && df == dir && tablero.get(d) != null) return true;
        return false;
    }

    private boolean validarTorre(String o, String d) {
        int dc = col(d) - col(o), df = fil(d) - fil(o);
        if (dc != 0 && df != 0) return false;
        int sc = Integer.signum(dc), sf = Integer.signum(df);
        int c = col(o) + sc, f = fil(o) + sf;
        while (c != col(d) || f != fil(d)) {
            if (tablero.get(casilla(c, f)) != null) return false;
            c += sc; f += sf;
        }
        return true;
    }

    private boolean validarAlfil(String o, String d) {
        int dc = col(d) - col(o), df = fil(d) - fil(o);
        if (Math.abs(dc) != Math.abs(df)) return false;
        int sc = Integer.signum(dc), sf = Integer.signum(df);
        int c = col(o) + sc, f = fil(o) + sf;
        while (c != col(d) || f != fil(d)) {
            if (tablero.get(casilla(c, f)) != null) return false;
            c += sc; f += sf;
        }
        return true;
    }

    private boolean validarCaballo(String o, String d) {
        int dc = Math.abs(col(d) - col(o)), df = Math.abs(fil(d) - fil(o));
        return (dc == 2 && df == 1) || (dc == 1 && df == 2);
    }

    private boolean validarRey(String o, String d) {
        return Math.abs(col(d) - col(o)) <= 1 && Math.abs(fil(d) - fil(o)) <= 1;
    }

    private String encontrarRey(boolean blancas, Map<String, String> tab) {
        String buscada = blancas ? "blancas_rey" : "negras_rey";
        for (Map.Entry<String, String> e : tab.entrySet()) {
            if (buscada.equals(e.getValue())) return e.getKey();
        }
        return null;
    }

    public boolean estaEnJaque(boolean blancas) {
        return estaEnJaqueEnTablero(blancas, this.tablero);
    }

    private boolean estaEnJaqueEnTablero(boolean blancas, Map<String, String> tab) {
        String posRey = encontrarRey(blancas, tab);
        if (posRey == null) return false;
        String prefEnemigo = blancas ? "negras" : "blancas";
        for (Map.Entry<String, String> e : tab.entrySet()) {
            if (!e.getValue().startsWith(prefEnemigo)) continue;
            ValidadorMovimiento tmp = new ValidadorMovimiento(tab);
            if (tmp.puedeAtacar(e.getKey(), posRey)) return true;
        }
        return false;
    }

    private boolean puedeAtacar(String origen, String destino) {
        String pieza = tablero.get(origen);
        if (pieza == null || origen.equals(destino)) return false;
        String tipo = pieza.split("_")[1];
        switch (tipo) {
            case "peon":    return puedeAtacarPeon(origen, destino, pieza.startsWith("blancas"));
            case "torre":   return validarTorre(origen, destino);
            case "alfil":   return validarAlfil(origen, destino);
            case "dama":    return validarTorre(origen, destino) || validarAlfil(origen, destino);
            case "caballo": return validarCaballo(origen, destino);
            case "rey":     return validarRey(origen, destino);
            default:        return false;
        }
    }

    private boolean puedeAtacarPeon(String o, String d, boolean esBlanca) {
        int dc = col(d) - col(o), df = fil(d) - fil(o);
        int dir = esBlanca ? 1 : -1;
        return Math.abs(dc) == 1 && df == dir;
    }

    private ValidadorMovimiento(Map<String, String> tab) {
        this.tablero.putAll(tab);
    }

    public boolean reyFueCapturado(boolean blancas) {
        return encontrarRey(blancas, this.tablero) == null;
    }

    public boolean estaEnJaqueMate(boolean blancas) {
        String prefPropio = blancas ? "blancas" : "negras";
        List<String> todasLasCasillas = generarTodasCasillas();

        for (Map.Entry<String, String> entry : new ArrayList<>(tablero.entrySet())) {
            if (!entry.getValue().startsWith(prefPropio)) continue;
            String origen = entry.getKey();
            for (String destino : todasLasCasillas) {
                if (origen.equals(destino)) continue;
                ValidadorMovimiento sim = new ValidadorMovimiento(this.tablero);
                boolean esBlancaLocal = entry.getValue().startsWith("blancas");
                if (!sim.validar(origen, destino, esBlancaLocal)) continue;
                Map<String, String> tabSim = new HashMap<>(this.tablero);
                tabSim.put(destino, tabSim.remove(origen));
                if (!estaEnJaqueEnTablero(blancas, tabSim)) return false;
            }
        }
        return true;
    }

    public boolean estaEnAhogado(boolean blancas) {
        if (estaEnJaque(blancas)) return false;

        String prefPropio = blancas ? "blancas" : "negras";
        List<String> todasLasCasillas = generarTodasCasillas();

        for (Map.Entry<String, String> entry : new ArrayList<>(tablero.entrySet())) {
            if (!entry.getValue().startsWith(prefPropio)) continue;
            String origen = entry.getKey();
            for (String destino : todasLasCasillas) {
                if (origen.equals(destino)) continue;
                ValidadorMovimiento sim = new ValidadorMovimiento(this.tablero);
                boolean esBlancaLocal = entry.getValue().startsWith("blancas");
                if (!sim.validar(origen, destino, esBlancaLocal)) continue;
                Map<String, String> tabSim = new HashMap<>(this.tablero);
                tabSim.put(destino, tabSim.remove(origen));
                if (!estaEnJaqueEnTablero(blancas, tabSim)) return false;
            }
        }
        return true;
    }

    private List<String> generarTodasCasillas() {
        List<String> lista = new ArrayList<>();
        for (char c = 'a'; c <= 'h'; c++) {
            for (char f = '1'; f <= '8'; f++) {
                lista.add("" + c + f);
            }
        }
        return lista;
    }
}
