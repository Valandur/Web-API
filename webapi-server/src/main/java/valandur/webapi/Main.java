package valandur.webapi;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import valandur.webapi.ipcomm.ws.WSLink;
import valandur.webapi.ipcomm.ws.WSServlet;

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

        MainHandler handler = new MainHandler(new WSLink());
        ContextHandler context = new ContextHandler();
        context.setContextPath("/api");
        context.setHandler(handler);
        handlers.addHandler(context);

        ServletContextHandler servletHandler = new ServletContextHandler();
        servletHandler.setContextPath("/");
        servletHandler.addServlet(WSServlet.class, "/ws");
        handlers.addHandler(servletHandler);

        server.setHandler(handlers);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
