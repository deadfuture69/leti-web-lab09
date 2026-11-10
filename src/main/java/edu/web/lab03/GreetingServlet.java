package edu.web.lab03;

import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(value = "/hello", initParams = @WebInitParam(
        name = "heading", value = "Добро пожаловать в веб-лабораторию"))
public class GreetingServlet extends HttpServlet {
    private String heading;

    @Override
    public void init() {
        heading = getInitParameter("heading");
        if (heading == null || heading.isBlank()) {
            heading = "Добро пожаловать в веб-лабораторию";
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String message = "Введите имя участника практикума.";
        if (name != null) {
            if (name.trim().length() < 4 || name.trim().length() > 40) {
                message = "Введите имя длиной от 4 до 40 символов.";
            } else {
                message = "Привет, " + name.trim() + "! Сервер подтвердил заявку на практикум.";
            }
        }
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().print("""
                <!DOCTYPE html>
                <html lang="ru">
                <head><meta charset="UTF-8"><title>Java Servlet</title></head>
                <body>
                    <h1>%s</h1>
                    <form action="hello" method="post">
                        <label>Имя <input name="name" minlength="4" maxlength="40" required></label>
                        <button type="submit">Отправить заявку</button>
                    </form>
                    <p>%s</p>
                </body>
                </html>
                """.formatted(escape(heading), escape(message)));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        doGet(request, response);
    }

    private String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
