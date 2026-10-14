package com.ejemplo.controller;

import com.ejemplo.model.ConexionBD;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

// Crea las tablas al arrancar si todavia no existen (schema.sql)
@WebListener
public class InicioBD implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (InputStream in = InicioBD.class.getResourceAsStream("/schema.sql");
             Connection con = ConexionBD.getConnection();
             Statement st = con.createStatement()) {
            String script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            for (String sentencia : script.split(";")) {
                if (!sentencia.isBlank()) {
                    st.execute(sentencia);
                }
            }
            System.out.println("Base de datos lista");
        } catch (IOException | SQLException e) {
            System.err.println("No se pudieron crear las tablas de la base de datos");
            e.printStackTrace();
        }
    }
}
