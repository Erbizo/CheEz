package com.ejemplo.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String DB_HOST     = System.getenv().getOrDefault("MYSQLHOST",     System.getenv().getOrDefault("DB_HOST",     "localhost"));
    private static final String DB_PORT     = System.getenv().getOrDefault("MYSQLPORT",     System.getenv().getOrDefault("DB_PORT",     "3306"));
    private static final String DB_NAME     = System.getenv().getOrDefault("MYSQLDATABASE", System.getenv().getOrDefault("DB_NAME",     "cheezdb"));
    private static final String DB_USER     = System.getenv().getOrDefault("MYSQLUSER",     System.getenv().getOrDefault("DB_USER",     "root"));
    private static final String DB_PASSWORD = System.getenv().getOrDefault("MYSQLPASSWORD", System.getenv().getOrDefault("DB_PASSWORD", "root"));

    private static final String URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se pudo cargar el driver de MySQL", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }
}
