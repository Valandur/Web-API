package io.valandur.webapi.common.web;

import io.valandur.webapi.common.WebAPIBase;
import io.valandur.webapi.common.graphql.GraphQLServlet;
import io.valandur.webapi.common.security.SecurityFilter;
import io.valandur.webapi.logger.Logger;
import java.net.SocketException;
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

public class WebServer {

  private final WebAPIBase<?> webapi;
  private final Logger logger;
  private final String basePath;
  private final String host;
  private final int portHttp;
  private final int minThreads;
  private final int maxThreads;
  private final int idleTimeout;

  private Server server;

  public WebServer(WebAPIBase<?> webapi) {
    this.webapi = webapi;
    this.logger = webapi.getLogger();

    var config = webapi.getServerConfig();
    try {
      config.load();
    } catch (Exception e) {
      webapi.getLogger().error("Could not load config: " + e.getMessage());
    }

    basePath = config.getBasePath();
    host = config.getHost();
    portHttp = config.getPort();
    minThreads = config.getMinThreads();
    maxThreads = config.getMaxThreads();
    idleTimeout = config.getIdleTimeout();

    try {
      config.save();
    } catch (Exception e) {
      webapi.getLogger().error("Could not save config: " + e.getMessage());
    }
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
        ServerConnector httpConn = new ServerConnector(server,
            new HttpConnectionFactory(httpConfig));
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
      for (var servlet : webapi.getServlets()) {
        conf.register(servlet);
      }
      conf.register(GraphQLServlet.class);

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
      logger.error(
          "Web-API web server could not start, probably because one of the ports needed for HTTP " +
              "and/or HTTPS are in use or not accessible (ports below 1024 are protected)");
    } catch (MultiException e) {
      e.getThrowables().forEach(t -> {
        if (t instanceof SocketException) {
          logger.error(
              "Web-API web server could not start, probably because one of the ports needed for " +
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
