package edu.web.lab03;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/hello")
public class GreetingServlet extends HttpServlet {
    private String heading;

    @Override
    public void init() {
        heading = "Добро пожаловать в веб-лабораторию";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().print("""
                <!DOCTYPE html>
                <html lang="ru">
                <head><meta charset="UTF-8"><title>Java Servlet</title></head>
                <body><h1>%s</h1><p>Ответ сформирован методом doGet.</p></body>
                </html>
                """.formatted(heading));
    }
}
