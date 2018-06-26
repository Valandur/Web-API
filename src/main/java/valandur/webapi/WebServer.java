package valandur.webapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.jaxrs.config.BeanConfig;
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
import valandur.webapi.config.MainConfig;
import valandur.webapi.handler.AssetHandler;
import valandur.webapi.handler.ErrorHandler;
import valandur.webapi.serialize.SerializationFeature;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class WebServer {

    private Logger logger;

    private Server server;

    private MainConfig config;

    private byte[] apConfig;

    public String getHost() {
        return config.host;
    }
    public int getHttpPort() {
        return config.http;
    }
    public int getHttpsPort() {
        return config.https;
    }


    WebServer(Logger logger, MainConfig config) {
        this.logger = logger;
        this.config = config;

        // Process the config.js file to include data from the Web-API config files
        try {
            MainConfig.APConfig cfg = config.adminPanelConfig;
            if (cfg != null) {
                ObjectMapper om = new ObjectMapper();
                String configStr = "window.config = " + om.writeValueAsString(cfg);
                apConfig = configStr.getBytes(Charset.forName("utf-8"));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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
            if (config.http >= 0) {
                if (config.http < 1024) {
                    logger.warn("You are using an HTTP port < 1024 which is not recommended! \n" +
                            "This might cause errors when not running the server as root/admin. \n" +
                            "Running the server as root/admin is not recommended. " +
                            "Please use a port above 1024 for HTTP."
                    );
                }
                ServerConnector httpConn = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
                httpConn.setHost(config.host);
                httpConn.setPort(config.http);
                httpConn.setIdleTimeout(30000);
                server.addConnector(httpConn);

                baseUri = "http://" + config.host + ":" + config.http;
            }

            // HTTPS
            if (config.https >= 0) {
                if (config.https < 1024) {
                    logger.warn("You are using an HTTPS port < 1024 which is not recommended! \n" +
                            "This might cause errors when not running the server as root/admin. \n" +
                            "Running the server as root/admin is not recommended. " +
                            "Please use a port above 1024 for HTTPS."
                    );
                }

                // Update http config
                httpConfig.setSecureScheme("https");
                httpConfig.setSecurePort(config.https);

                String loc = config.customKeyStore;
                String pw = config.customKeyStorePassword;
                String mgrPw = config.customKeyStoreManagerPassword;
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
                httpsConn.setHost(config.host);
                httpsConn.setPort(config.https);
                httpsConn.setIdleTimeout(30000);
                server.addConnector(httpsConn);

                baseUri = "https://" + config.host + ":" + config.https;
            }

            if (baseUri == null) {
                logger.error("You have disabled both HTTP and HTTPS - The Web-API will be unreachable!");
                baseUri = ""; // for swagger
            }

            // Collection of all handlers
            ContextHandlerCollection mainContext = new ContextHandlerCollection();

            // Asset handlers
            mainContext.addHandler(newContext("/docs", new AssetHandler("pages/redoc.html")));

            String panelPath = null;
            if (config.adminPanel) {
                // Rewrite handler
                RewriteHandler rewrite = new RewriteHandler();
                rewrite.setRewriteRequestURI(true);
                rewrite.setRewritePathInfo(true);

                panelPath = config.adminPanelConfig.basePath;
                if (!panelPath.startsWith("/")) {
                    panelPath = "/" + config.adminPanelConfig.basePath;
                }
                RedirectPatternRule redirect = new RedirectPatternRule();
                redirect.setPattern("/*");
                redirect.setLocation(panelPath);
                rewrite.addRule(redirect);
                mainContext.addHandler(newContext("/", rewrite));

                final String pPath = panelPath;
                mainContext.addHandler(newContext(panelPath, new AssetHandler("admin", path -> {
                    if (path.endsWith("config.js") && this.apConfig != null) {
                        return input -> apConfig;
                    }
                    if (path.endsWith("index.html")) {
                        return input -> new String(input).replace("<base href=\"/\">",
                                "<base href=\"" + pPath + "\">").getBytes();
                    }

                    return input -> input;
                })));
            }

            // Main servlet context
            ServletContextHandler servletsContext = new ServletContextHandler();
            servletsContext.setContextPath(Constants.BASE_PATH);

            // Resource config for jersey servlet
            ResourceConfig conf = new ResourceConfig();
            conf.packages(
                    "io.swagger.jaxrs.listing",
                    "valandur.webapi.shadow.io.swagger.jaxrs.listing",
                    "valandur.webapi.handler",
                    "valandur.webapi.security",
                    "valandur.webapi.serialize",
                    "valandur.webapi.user"
                    //"io.swagger.v3.jaxrs2.integration.resources"                      // This if for Swagger 3.0
            );
            conf.property("jersey.config.server.wadl.disableWadl", true);

            // Add error handler to jetty (will also be picked up by jersey
            ErrorHandler errHandler = new ErrorHandler();
            server.setErrorHandler(errHandler);
            server.addBean(errHandler);

            // Register all servlets. We use this instead of package scanning because we don't want the
            // integrated servlets to load unless their plugin is present. Also this gives us more control/info
            // over which servlets/endpoints are loaded.
            Set<String> servlets = new HashSet<>();
            for (Class<? extends BaseServlet> servletClass :
                    WebAPI.getServletService().getRegisteredServlets().values()) {
                conf.register(servletClass);
                String pkg = servletClass.getPackage().getName();
                servlets.add(pkg);
            }

            // Register serializer
            conf.register(SerializationFeature.class);

            // Jersey servlet
            ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(conf));
            jerseyServlet.setInitOrder(1);
            // This if for Swagger 3.0
            // jerseyServlet.setInitParameter("openApi.configuration.location", assets/webapi/swagger/config.json");                                    // This is for Swagger 3.0
            servletsContext.addServlet(jerseyServlet, "/*");

            // Register swagger as bean
            // TODO: We can't set scheme and host yet because Swagger 2.0 doesn't support multiple different ones
            BeanConfig beanConfig = new BeanConfig();
            beanConfig.setBasePath(Constants.BASE_PATH);
            beanConfig.setResourcePackage("valandur.webapi.swagger," + String.join(",", servlets));
            beanConfig.setScan(true);
            if (config.devMode) {
                beanConfig.setPrettyPrint(true);
            }
            servletsContext.addBean(beanConfig);

            // Attach error handler to servlets context
            servletsContext.setErrorHandler(errHandler);
            servletsContext.addBean(errHandler);

            // Add servlets to main context
            mainContext.addHandler(servletsContext);

            // Add main context to server
            server.setHandler(mainContext);


            server.start();

            if (config.adminPanel) {
                logger.info("AdminPanel: " + baseUri + panelPath);
            }
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
                WebAPI.sentryCapture(e);
            }
        }
    }

    public void handle(String target, Request baseRequest, HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        server.handle(target, baseRequest, req, res);
    }

    private ContextHandler newContext(String path, Handler handler) {
        ContextHandler context = new ContextHandler();
        context.setContextPath(path);
        context.setHandler(handler);
        return context;
    }
}
