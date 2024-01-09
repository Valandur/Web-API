package io.valandur.webapi.web;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;
import io.valandur.webapi.graphql.GraphQLServlet;
import io.valandur.webapi.security.SecurityFilter;
import java.net.SocketException;
import java.util.stream.Collectors;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class WebServer<T extends WebAPI<?, ?>> extends Service<T> {

  private Server server;

  private String basePath;
  private String host;
  private int portHttp;
  private int minThreads;
  private int maxThreads;
  private int idleTimeout;


  public WebServer(T webapi) {
    super(webapi);
  }

  @Override
  public void init() {
    super.init();

    var config = webapi.getServerConfig();
    try {
      config.load();
    } catch (Exception e) {
      logger.error("Could not load config: " + e.getMessage());
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
      logger.error("Could not save config: " + e.getMessage());
    }
  }

  @Override
  public void start() {
    // Start web server
    logger.info("Starting web server...");

    try {
      var threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);
      threadPool.setName(WebAPI.NAME);
      server = new Server(threadPool);
      server.setErrorHandler(new ErrorHandler());
      server.insertHandler(new GzipHandler());

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

        var http = new HttpConnectionFactory(new HttpConfiguration());
        var http2 = new HTTP2CServerConnectionFactory(new HttpConfiguration());
        var conn = new ServerConnector(server, http, http2);
        conn.setHost(host);
        conn.setPort(portHttp);
        conn.setIdleTimeout(idleTimeout);
        server.addConnector(conn);

        baseUri = "http://" + host + ":" + portHttp;
      }

      if (baseUri == null) {
        logger.error("You have disabled both HTTP and HTTPS - The Web-API will be unreachable!");
      }

      var handler = generateHandler();
      server.setHandler(handler);

      server.start();

      logger.info("Web server running: " + baseUri);
    } catch (SocketException e) {
      logger.error(
          "Web-API web server could not start, probably because one of the ports needed for HTTP " +
              "and/or HTTPS are in use or not accessible (ports below 1024 are protected)");
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  private ServletContextHandler generateHandler() {
    var servletsContext = new ServletContextHandler();
    servletsContext.setContextPath(basePath);

    var jerseyHolder = generateServletHolder();
    servletsContext.addServlet(jerseyHolder, "/*");

    return servletsContext;
  }

  private ServletHolder generateServletHolder() {
    var conf = new ResourceConfig();
    conf.register(CorsFilter.class);
    conf.register(JacksonJsonProvider.class);
    conf.register(ObjectMapperProvider.class);
    conf.register(SecurityFilter.class);
    for (var servlet : webapi.getServlets()) {
      // TODO: Change to fix warnings
      conf.register(servlet);
    }
    conf.register(GraphQLServlet.class);
    conf.register(generateOpenAPIResource());
    conf.register(ErrorHandler.class);

    conf.property("jersey.config.server.wadl.disableWadl", true);
    conf.property("jersey.config.disableDefaultProvider", "ALL");

    var jerseyServlet = new ServletContainer(conf);

    return new ServletHolder(jerseyServlet);
  }

  private OpenApiResource generateOpenAPIResource() {
    var info = new Info()
        .title("Web-API")
        .description("RESTful Web-API for minecraft servers")
        .version(webapi.getVersion());

    var oas = new OpenAPI()
        .info(info);

    var oasConfig = new SwaggerConfiguration()
        .openAPI(oas)
        .prettyPrint(true)
        .resourceClasses(
            webapi.getServlets().stream().map(s -> s.getClass().getName())
                .collect(Collectors.toSet())
        );

    var res = new OpenApiResource();
    res.setOpenApiConfiguration(oasConfig);
    return res;
  }

  @Override
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
