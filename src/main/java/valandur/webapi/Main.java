package valandur.webapi;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.TimeUnit;

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

        server.setHandler(new MainHandler());

        WebSocketClient client = new WebSocketClient();
        WSSocket socket = new WSSocket();
        try
        {
            server.start();

            client.start();

            URI echoUri = new URI("ws://localhost:8080/api/v5/ws");
            System.out.printf("Connecting to : %s%n",echoUri);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, echoUri, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
