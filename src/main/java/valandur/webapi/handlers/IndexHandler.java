package valandur.webapi.handlers;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class IndexHandler extends AbstractHandler {

    private List<ContextHandler> handlers;

    public IndexHandler(List<ContextHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();
        out.print("<!DOCTYPE html><html><head></head><body>");

        for (ContextHandler handler : this.handlers) {
            out.print("<a href='" + handler.getContextPath() + "'>" + handler.getContextPath() + "</a><br />");
        }

        out.print("</body></html>");
        baseRequest.setHandled(true);
    }
}
