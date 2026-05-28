package com.ejemplo.controller;

import com.ejemplo.model.JugadorDAO;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.*;
import java.sql.SQLException;

@WebListener
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        String alias = (String) session.getAttribute("alias");
        if (alias != null) {
            try {
                new JugadorDAO().eliminar(alias);
            } catch (SQLException e) {
            }
        }
    }
}
