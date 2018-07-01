package valandur.webapi;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import valandur.webapi.link.LinkServer;
import valandur.webapi.link.rabbitmq.RabbitMQLinkServer;

import java.util.HashMap;

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

        LinkServer link = new RabbitMQLinkServer(new HashMap<>());

        MainHandler handler = new MainHandler(link);
        ContextHandler context = new ContextHandler();
        context.setContextPath("/api");
        context.setHandler(handler);
        handlers.addHandler(context);

        server.setHandler(handlers);

        link.init(handlers);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
