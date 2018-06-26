package valandur.webapi;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import valandur.webapi.ipcomm.IPLink;
import valandur.webapi.ipcomm.IPServlet;
import valandur.webapi.ipcomm.ws.WSLink;
import valandur.webapi.ipcomm.ws.WSServlet;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

public class Main {
    public static void main(String... args) {
        Server server = new Server();

        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setOutputBufferSize(32768);

        ServerConnector httpConn = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        httpConn.setHost("localhost");
        httpConn.setPort(5000);
        httpConn.setIdleTimeout(30000);
        server.addConnector(httpConn);

        ContextHandlerCollection handlers = new ContextHandlerCollection();

        IPLink link = new WSLink();
        link.init();

        MainHandler handler = new MainHandler(link);
        ContextHandler context = new ContextHandler();
        context.setContextPath("/api");
        context.setHandler(handler);
        handlers.addHandler(context);

        ServletContextHandler servletHandler = new ServletContextHandler();
        servletHandler.setContextPath("/");
        ServletHolder holder = servletHandler.addServlet(link.getServletClass(), "/ws");
        handlers.addHandler(servletHandler);

        server.setHandler(handlers);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            IPServlet servlet = (IPServlet) holder.getServlet();
            servlet.init(link);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
}
