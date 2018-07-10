package valandur.webapi.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import valandur.webapi.link.LinkType;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class ServerConfig extends BaseConfig {

    @Setting(comment = "The host on which the web server should listen for incoming connections.\n" +
            "Use \"0.0.0.0\" to listen on all bound addresses.")
    public String host = "localhost";

    @Setting(comment = "The port on which the web server should listen.\n" +
            "Ports below 1024 may be protected by the OS, so choosing something about is recommended.")
    public int port = 5000;

    @Setting(comment = "The type of link to establish with the Minecraft servers.\n" +
            "Currently supported are \"WebSocket\", \"Redis\" or \"RabbitMQ\".")
    public LinkType type = LinkType.WebSocket;

    @Setting(comment = "A map of Minecraft servers that are accepted by this server.\n" +
            "This mapping goes from name to key. Choose something secure and long for your key, \n" +
            "and put it into the \"privateKey\" field in the \"link.conf\" file of that Minecraft server")
    public Map<String, String> servers = new HashMap<>();


    // Add default map values
    public ServerConfig() {
        servers.put("server1", "CHANGE_ME_TO_SOMETHING_BETTER");
    }
}
