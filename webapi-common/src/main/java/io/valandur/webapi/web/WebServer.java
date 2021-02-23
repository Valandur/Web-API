package io.valandur.webapi.web;

import io.valandur.webapi.WebAPI;
import io.valandur.webapi.config.Config;
import io.valandur.webapi.graphql.GraphQLServlet;
import io.valandur.webapi.info.InfoServlet;
import io.valandur.webapi.player.PlayerServlet;
import io.valandur.webapi.user.UserServlet;
import io.valandur.webapi.world.WorldServlet;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.MultiException;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;

import java.net.SocketException;

public class WebServer {

    private final WebAPI<?> webapi;
    private Logger logger;

    private Server server;

    private String basePath;
    private String host;
    private int portHttp;
    private int minThreads;
    private int maxThreads;
    private int idleTimeout;

    public WebServer(WebAPI<?> webapi) {
        this.webapi = webapi;
    }

    public void load() {
        logger = webapi.getLogger();

        Config conf = webapi.getConfig("web");
        conf.load();

        basePath = conf.get("basePath", "/");
        host = conf.get("host", "0.0.0.0");
        portHttp = conf.get("portHttp", 8080);
        minThreads = conf.get("minThreads", 1);
        maxThreads = conf.get("maxThreads", 5);
        idleTimeout = conf.get("idleTimeout", 60);
    }

    public void start() {
        // Start web server
        logger.info("Starting Web Server...");

        try {

            QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);
            threadPool.setName("API-POOL");
            server = new Server(threadPool);

            // Add error handler to jetty (will also be picked up by jersey
            ErrorHandler errHandler = new ErrorHandler();
            server.setErrorHandler(errHandler);
            server.addBean(errHandler);

            // HTTP config
            HttpConfiguration httpConfig = new HttpConfiguration();

            String baseUri = null;

            // HTTP
            if (portHttp >= 0) {
                if (portHttp < 1024) {
                    logger.warn("You are using an HTTP port < 1024 which is not recommended! \n" +
                            "This might cause errors when not running the server as root/admin. \n" +
                            "Running the server as root/admin is not recommended. " +
                            "Please use a port above 1024 for HTTP."
                    );
                }
                ServerConnector httpConn = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
                httpConn.setHost(host);
                httpConn.setPort(portHttp);
                httpConn.setIdleTimeout(idleTimeout);
                server.addConnector(httpConn);

                baseUri = "http://" + host + ":" + portHttp;
            }

            if (baseUri == null) {
                logger.error("You have disabled both HTTP and HTTPS - The Web-API will be unreachable!");
            }

            // Servlet context
            ServletContextHandler servletsContext = new ServletContextHandler();
            servletsContext.setContextPath(basePath);

            // GraphQL
            servletsContext.addServlet(GraphQLServlet.class, "/graphql");

            ResourceConfig conf = new ResourceConfig();
            conf.register(WorldServlet.class);
            conf.register(WorldServlet.class.getPackage().getName());

            conf.register(PlayerServlet.class);
            conf.register(PlayerServlet.class.getPackage().getName());

            conf.register(UserServlet.class);
            conf.register(UserServlet.class.getPackage().getName());

            conf.register(InfoServlet.class);
            conf.register(InfoServlet.class.getPackage().getName());

            conf.register(JacksonFeature.class);

            ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(conf));
            jerseyServlet.setInitOrder(0);
            servletsContext.addServlet(jerseyServlet, "/*");

            // Add main context to server
            server.setHandler(servletsContext);

            server.start();
        } catch (SocketException e) {
            logger.error("Web-API web server could not start, probably because one of the ports needed for HTTP " +
                    "and/or HTTPS are in use or not accessible (ports below 1024 are protected)");
        } catch (MultiException e) {
            e.getThrowables().forEach(t -> {
                if (t instanceof SocketException) {
                    logger.error("Web-API web server could not start, probably because one of the ports needed for " +
                            "HTTP and/or HTTPS are in use or not accessible (ports below 1024 are protected)");
                } else {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (server != null) {
            try {
                server.stop();
                server = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
