package valandur.webapi;

import com.google.common.io.Files;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import valandur.webapi.config.BaseConfig;
import valandur.webapi.config.ServerConfig;
import valandur.webapi.link.LinkServer;
import valandur.webapi.link.rabbitmq.RabbitMQLinkServer;
import valandur.webapi.link.redis.RedisLinkServer;
import valandur.webapi.link.ws.WSLinkServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static final Path configFilePath = Paths.get("config/server.conf");

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private static volatile Server server = new Server();


    public static void main(String... args) {
        logger.info("Starting Web-API server...");

        try {
            Files.createParentDirs(configFilePath.toFile());
        } catch (IOException e) {
            logger.error("Could not create config folder", e);
        }

        logger.info("Loading config file...");
        ServerConfig config = BaseConfig.load(configFilePath, new ServerConfig());

        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setOutputBufferSize(32768);

        ServerConnector httpConn = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        httpConn.setHost(config.host);
        httpConn.setPort(config.port);
        httpConn.setIdleTimeout(30000);
        server.addConnector(httpConn);

        ContextHandlerCollection handlers = new ContextHandlerCollection();

        LinkServer link = createLinkServer(config);
        if (link == null) {
            logger.error("No link type specified, exiting application!");
            System.exit(1);
            return;
        }

        MainHandler handler = new MainHandler(link);
        ContextHandler context = new ContextHandler();
        context.setContextPath("/api");
        context.setHandler(handler);
        handlers.addHandler(context);

        server.setHandler(handlers);

        link.init(handlers);

        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                logger.error("Server error", e);
                try {
                    server.stop();
                } catch (Exception ignored) { }
            }
        });
        serverThread.start();

        logger.info("Waiting for server to start...");

        long time = System.currentTimeMillis();
        while (!server.isStarted() && !server.isFailed()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}

            if (System.currentTimeMillis() - time > 30000) {
                logger.error("Failed to start Web-API server in 30 seconds, exiting!");
                serverThread.interrupt();
                System.exit(1);
                return;
            }
        }

        if (server.isFailed()) {
            System.exit(1);
            return;
        }

        logger.info("Web server running on " + config.host + ":" + config.port);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = null;

            try {
                System.out.print("> ");
                line = br.readLine();
            } catch (IOException e) {
                logger.warn("Could not read input", e);
            }

            if (line == null || line.isEmpty()) {
                continue;
            }

            switch (line.toLowerCase()) {
                case "help":
                    System.out.println("Available commands:");
                    System.out.println("  help    Shows this help page");
                    System.out.println("  list    List all servers");
                    System.out.println("  stop    Stops the web server");
                    break;

                case "list":
                case "servers":
                case "status":
                    System.out.println("--- Servers ---");
                    link.getServerStatus().forEach((name, status) -> {
                        System.out.println("  " + (status ? "✓ " : "  ") + name);
                    });
                    System.out.println("---------------");
                    break;

                case "exit":
                case "stop":
                    logger.info("Stopping Web-API server");
                    try {
                        server.stop();
                    } catch (Exception e) {
                        logger.warn("Could not stop web server", e);
                        logger.info("Interrupting server thread...");
                        serverThread.interrupt();
                    }
                    logger.info("Web-API server is done!");
                    return;

                default:
                    System.out.println("Unknown command. Use 'help' for a list of available commands.");
            }
        }
    }

    private static LinkServer createLinkServer(ServerConfig config) {
        switch (config.type) {
            case WebSocket:
                return new WSLinkServer(config.servers);
            case Redis:
                return new RedisLinkServer(config.servers);
            case RabbitMQ:
                return new RabbitMQLinkServer(config.servers);
        }
        return null;
    }
}
