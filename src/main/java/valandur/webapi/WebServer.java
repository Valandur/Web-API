package valandur.webapi;

import io.swagger.jaxrs.config.BeanConfig;
import ninja.leaping.configurate.ConfigurationNode;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.MultiException;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.handler.AssetHandler;
import valandur.webapi.handler.ErrorHandler;
import valandur.webapi.serialize.SerializationFeature;
import valandur.webapi.util.Constants;

import java.net.SocketException;

public class WebServer {

    private Logger logger;

    private boolean adminPanelEnabled;
    private String serverHost;
    private Integer serverPortHttp;
    private Integer serverPortHttps;
    private String keyStoreLocation;
    private String keyStorePassword;
    private String keyStoreMgrPassword;
    private Server server;

    public String getHost() {
        return serverHost;
    }
    public int getHttpPort() {
        return serverPortHttp;
    }
    public int getHttpsPort() {
        return serverPortHttps;
    }


    WebServer(Logger logger, ConfigurationNode config) {
        this.logger = logger;

        serverHost = config.getNode("host").getString("localhost");
        serverPortHttp = config.getNode("http").getInt(8080);
        serverPortHttps = config.getNode("https").getInt(8081);
        adminPanelEnabled = config.getNode("adminPanel").getBoolean(true);
        keyStoreLocation = config.getNode("customKeyStore").getString();
        keyStorePassword = config.getNode("customKeyStorePassword").getString();
        keyStoreMgrPassword = config.getNode("customKeyStoreManagerPassword").getString();
    }

    public void start(Player player) {
        // Start web server
        logger.info("Starting Web Server...");

        try {
            server = new Server();

            // HTTP config
            HttpConfiguration httpConfig = new HttpConfiguration();
            httpConfig.setOutputBufferSize(32768);

            String baseUri = null;

            // HTTP
            if (serverPortHttp >= 0) {
                if (serverPortHttp < 1024) {
                    logger.warn("You are using an HTTP port < 1024 which is not recommended! \n" +
                            "This might cause errors when not running the server as root/admin. \n" +
                            "Running the server as root/admin is not recommended. " +
                            "Please use a port above 1024 for HTTP."
                    );
                }
                ServerConnector httpConn = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
                httpConn.setHost(serverHost);
                httpConn.setPort(serverPortHttp);
                httpConn.setIdleTimeout(30000);
                server.addConnector(httpConn);

                baseUri = "http://" + serverHost + ":" + serverPortHttp;
            }

            // HTTPS
            if (serverPortHttps >= 0) {
                if (serverPortHttps < 1024) {
                    logger.warn("You are using an HTTPS port < 1024 which is not recommended! \n" +
                            "This might cause errors when not running the server as root/admin. \n" +
                            "Running the server as root/admin is not recommended. " +
                            "Please use a port above 1024 for HTTPS."
                    );
                }

                // Update http config
                httpConfig.setSecureScheme("https");
                httpConfig.setSecurePort(serverPortHttps);

                String loc = keyStoreLocation;
                String pw = keyStorePassword;
                String mgrPw = keyStoreMgrPassword;
                if (loc == null || loc.isEmpty()) {
                    loc = Sponge.getAssetManager().getAsset(WebAPI.getInstance(), "keystore.jks")
                            .map(a -> a.getUrl().toString()).orElse("");
                    pw = "mX4z%&uJ2E6VN#5f";
                    mgrPw = "mX4z%&uJ2E6VN#5f";
                }

                // SSL Factory
                SslContextFactory sslFactory = new SslContextFactory();
                sslFactory.setKeyStorePath(loc);
                sslFactory.setKeyStorePassword(pw);
                sslFactory.setKeyManagerPassword(mgrPw);

                // HTTPS config
                HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
                SecureRequestCustomizer src = new SecureRequestCustomizer();
                src.setStsMaxAge(2000);
                src.setStsIncludeSubDomains(true);
                httpsConfig.addCustomizer(src);


                ServerConnector httpsConn = new ServerConnector(server,
                        new SslConnectionFactory(sslFactory, HttpVersion.HTTP_1_1.asString()),
                        new HttpConnectionFactory(httpsConfig)
                );
                httpsConn.setHost(serverHost);
                httpsConn.setPort(serverPortHttps);
                httpsConn.setIdleTimeout(30000);
                server.addConnector(httpsConn);

                baseUri = "https://" + serverHost + ":" + serverPortHttps;
            }

            if (baseUri == null) {
                logger.error("You have disabled both HTTP and HTTPS - The Web-API will be unreachable!");
                baseUri = ""; // for swagger
            }

            // Collection of all handlers
            ContextHandlerCollection mainContext = new ContextHandlerCollection();

            // Asset handlers
            mainContext.addHandler(newContext("/docs", new AssetHandler("pages/redoc.html")));

            if (adminPanelEnabled) {
                // Rewrite handler
                RewriteHandler rewrite = new RewriteHandler();
                rewrite.setRewriteRequestURI(true);
                rewrite.setRewritePathInfo(true);

                RedirectPatternRule redirect = new RedirectPatternRule();
                redirect.setPattern("/*");
                redirect.setLocation("/admin");
                rewrite.addRule(redirect);
                mainContext.addHandler(newContext("/", rewrite));

                mainContext.addHandler(newContext("/admin", new AssetHandler("admin")));
            }

            // Main servlet context
            ServletContextHandler servletsContext = new ServletContextHandler();
            servletsContext.setContextPath(Constants.BASE_PATH);

            // Resource config for jersey servlet
            ResourceConfig config = new ResourceConfig();
            config.packages(
                    "io.swagger.jaxrs.listing",
                    "valandur.webapi.handler",
                    "valandur.webapi.security",
                    "valandur.webapi.serialize",
                    "valandur.webapi.user"
                    //"io.swagger.v3.jaxrs2.integration.resources"                      // This if for Swagger 3.0
            );
            config.property("jersey.config.server.wadl.disableWadl", true);

            // Register all servlets. We use this instead of package scanning because we don't want the
            // integrated servlets to load unless their plugin is present. Also this gives us more control/info
            // over which servlets/endpoints are loaded.
            StringBuilder swaggerPath = new StringBuilder();
            for (Class<? extends BaseServlet> servletClass :
                    WebAPI.getServletService().getRegisteredServlets().values()) {
                config.register(servletClass);
                swaggerPath.append(",").append(servletClass.getPackage().getName());
            }

            // Register serializer
            config.register(SerializationFeature.class);

            // Jersey servlet
            ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(config));
            jerseyServlet.setInitOrder(1);
            // jerseyServlet.setInitParameter("openApi.configuration.location", assets/webapi/swagger/config.json");                                    // This is for Swagger 3.0
            servletsContext.addServlet(jerseyServlet, "/*");

            // Register swagger as bean
            // TODO: We can't set scheme and host yet because Swagger 2.0 doesn't support multiple different ones
            BeanConfig beanConfig = new BeanConfig();
            beanConfig.setBasePath(Constants.BASE_PATH);
            beanConfig.setResourcePackage("valandur.webapi.swagger" + swaggerPath);
            beanConfig.setScan(true);
            beanConfig.setPrettyPrint(true);
            servletsContext.addBean(beanConfig);

            // Add servlets to main context
            mainContext.addHandler(servletsContext);

            // Add main context to server
            server.setHandler(mainContext);

            // Add error handler to jetty (will also be picked up by jersey
            ErrorHandler errHandler = new ErrorHandler();
            server.setErrorHandler(errHandler);
            server.addBean(errHandler);

            server.start();

            if (adminPanelEnabled)
                logger.info("AdminPanel: " + baseUri + "/admin");
            logger.info("API Docs: " + baseUri + "/docs");
        } catch (SocketException e) {
            logger.error("Web-API webserver could not start, probably because one of the ports needed for HTTP " +
                    "and/or HTTPS are in use or not accessible (ports below 1024 are protected)");
        } catch (MultiException e) {
            e.getThrowables().forEach(t -> {
                if (t instanceof SocketException) {
                    logger.error("Web-API webserver could not start, probably because one of the ports needed for HTTP " +
                            "and/or HTTPS are in use or not accessible (ports below 1024 are protected)");
                } else {
                    t.printStackTrace();
                    WebAPI.sentryCapture(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            WebAPI.sentryCapture(e);
        }

        if (player != null) {
            player.sendMessage(Text.builder()
                    .color(TextColors.AQUA)
                    .append(Text.of("[" + Constants.NAME + "] The web server has been restarted!"))
                    .toText()
            );
        }
    }

    public void stop() {
        if (server != null) {
            try {
                server.stop();
                server = null;
            } catch (Exception e) {
                e.printStackTrace();
                if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);
            }
        }
    }

    private ContextHandler newContext(String path, Handler handler) {
        ContextHandler context = new ContextHandler();
        context.setContextPath(path);
        context.setHandler(handler);
        return context;
    }
}
