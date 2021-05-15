package io.valandur.webapi.web;

import io.leangen.geantyref.TypeToken;
import io.valandur.webapi.WebAPI;
import io.valandur.webapi.config.Config;
import io.valandur.webapi.graphql.GraphQLServlet;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerServlet;
import io.valandur.webapi.security.SecurityFilter;
import io.valandur.webapi.server.ServerServlet;
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

import java.net.SocketException;

public class WebServer {

    private final WebAPI<?, ?> webapi;
    private final Logger logger;

    private Server server;

    private String basePath;
    private String host;
    private int portHttp;
    private int minThreads;
    private int maxThreads;
    private int idleTimeout;

    public WebServer(WebAPI<?, ?> webapi) {
        this.webapi = webapi;
        this.logger = webapi.getLogger();
    }

    public void load() {
        Config conf = webapi.getConfig("web");
        conf.load();

        basePath = conf.get("basePath", TypeToken.get(String.class), "/");
        host = conf.get("host", TypeToken.get(String.class), "0.0.0.0");
        portHttp = conf.get("portHttp", TypeToken.get(Integer.class), 8080);
        minThreads = conf.get("minThreads", TypeToken.get(Integer.class), 1);
        maxThreads = conf.get("maxThreads", TypeToken.get(Integer.class), 5);
        idleTimeout = conf.get("idleTimeout", TypeToken.get(Integer.class), 60);
    }

    public void start() {
        // Start web server
        logger.info("Starting web server...");

        try {
            var threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);
            threadPool.setName("WEB-API-POOL");
            server = new Server(threadPool);
            server.setErrorHandler(new ErrorHandler());

            // HTTP config
            var httpConfig = new HttpConfiguration();

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
            var servletsContext = new ServletContextHandler();
            servletsContext.setContextPath(basePath);

            var conf = new ResourceConfig();
            conf.register(JacksonFeature.class);

            conf.register(SecurityFilter.class);

            conf.register(ErrorHandler.class);

            conf.register(GraphQLServlet.class);
            conf.register(WorldServlet.class);
            conf.register(PlayerServlet.class);
            conf.register(ServerServlet.class);

            conf.property("jersey.config.server.wadl.disableWadl", true);

            // Jersey
            var jerseyServlet = new ServletContainer(conf);
            var jerseyHolder = new ServletHolder(jerseyServlet);
            servletsContext.addServlet(jerseyHolder, "/*");

            // Add main context to server
            server.setHandler(servletsContext);

            server.start();

            logger.info("Web server running: " + baseUri);
        } catch (SocketException e) {
            logger.error("Web-API web server could not start, probably because one of the ports needed for HTTP " +
                    "and/or HTTPS are in use or not accessible (ports below 1024 are protected)");
        } catch (MultiException e) {
            e.getThrowables().forEach(t -> {
                if (t instanceof SocketException) {
                    logger.error("Web-API web server could not start, probably because one of the ports needed for " +
                            "HTTP and/or HTTPS are in use or not accessible (ports below 1024 are protected)");
                } else {
                    logger.error(t.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            try {
                server.stop();
                server = null;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}
