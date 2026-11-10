package edu.web.lab03;

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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GreetingServletTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private GreetingServlet servlet;

    @BeforeEach
    void initializeServlet() {
        servlet = new GreetingServlet();
        servlet.init();
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
    void writesPageWithRussianHeading() throws IOException {
        StringWriter output = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(output));
        servlet.doGet(request, response);

        String html = output.toString().strip();
        assertTrue(html.startsWith("<!DOCTYPE html>"));
        assertTrue(html.contains("<h1>Добро пожаловать в веб-лабораторию</h1>"));
        assertTrue(html.endsWith("</html>"));
    }

    @Test
    void staticGreetingDoesNotRequireRequestData() throws IOException {
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        servlet.doGet(request, response);

        verifyNoInteractions(request);
    }

    @Test
    void reportsFailureToOpenResponseWriter() throws IOException {
        IOException failure = new IOException("Response unavailable");
        when(response.getWriter()).thenThrow(failure);

        assertSame(failure, assertThrows(IOException.class, () -> servlet.doGet(request, response)));
    }
}
