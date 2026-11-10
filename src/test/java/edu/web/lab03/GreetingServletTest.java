package edu.web.lab03;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GreetingServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletConfig config;

    private GreetingServlet servlet;

    @BeforeEach
    void initializeServlet() throws ServletException {
        servlet = new GreetingServlet();
        servlet.init(config);
    }

    @Test
    void declaresUtf8HtmlBeforeOpeningWriter() throws IOException {
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        servlet.doGet(request, response);
        var order = inOrder(response);
        order.verify(response).setContentType("text/html; charset=UTF-8");
        order.verify(response).getWriter();
    }

    @Test
    void writesPageWithDefaultHeading() throws IOException {
        String html = render();
        assertTrue(html.startsWith("<!DOCTYPE html>"));
        assertTrue(html.contains("<h1>Добро пожаловать в веб-лабораторию</h1>"));
        assertTrue(html.endsWith("</html>"));
    }

    @Test
    void usesInitializationParameter() throws Exception {
        when(config.getInitParameter("heading")).thenReturn("Практикум по Java");
        servlet.init(config);
        assertTrue(render().contains("<h1>Практикум по Java</h1>"));
    }

    @Test
    void readsNameFromRequest() throws IOException {
        when(request.getParameter("name")).thenReturn("Лера");
        assertTrue(render().contains("Привет, Лера! Сервер подтвердил заявку на практикум."));
    }

    @Test
    void escapesUserInput() throws IOException {
        when(request.getParameter("name")).thenReturn("<b>Лера</b>");
        assertTrue(render().contains("&lt;b&gt;Лера&lt;/b&gt;"));
    }

    @Test
    void validatesShortName() throws IOException {
        when(request.getParameter("name")).thenReturn("Ян");
        assertTrue(render().contains("Введите имя длиной от 4 до 40 символов."));
    }

    @Test
    void decodesPostBeforeReadingParameter() throws IOException {
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        servlet.doPost(request, response);
        var order = inOrder(request);
        order.verify(request).setCharacterEncoding("UTF-8");
        order.verify(request).getParameter("name");
    }

    @Test
    void reportsFailureToOpenResponseWriter() throws IOException {
        IOException failure = new IOException("Response unavailable");
        when(response.getWriter()).thenThrow(failure);
        assertSame(failure, assertThrows(IOException.class, () -> servlet.doGet(request, response)));
    }

    private String render() throws IOException {
        StringWriter output = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(output));
        servlet.doGet(request, response);
        return output.toString().strip();
    }
}
