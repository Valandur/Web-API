package valandur.webapi.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class MainConfig extends BaseConfig {

    @Setting(comment = "Set this to false to completely disable the admin panel")
    public boolean adminPanel = true;

    @Setting(comment = "Change AdminPanel settings")
    public APConfig adminPanelConfig = new APConfig();

    @Setting(comment = "Set this to true when working on the WebAPI. \n" +
            "This is NOT detailed debug info, so don't turn on if not \n" +
            "running the Web-API from an IDE")
    public boolean devMode = false;

    @Setting(comment = "This is the host the API will be listening on.\n" +
            "Default is \"localhost\" to prevent any access from outside\n" +
            "the server, but you can set this to \"0.0.0.0\" to listen\n" +
            "on all addresses IF YOU HAVE CONFIGURED THE PERMISSIONS")
    public String host = "localhost";

    @Setting(comment = "This tells the API on which port to listen for HTTP requests.\n" +
            "It is recommended to use a port above 1024, as those below\n" +
            "are reserved for the system and might not be available.\n" +
            "Set to -1 to disable the HTTP protocol.")
    public int http = 8080;
    @Setting(comment = "This tells the API on which port to listen for HTTPS requests.\n" +
            "It is recommended to use a port above 1024, as those below\n" +
            "are reserved for the system and might not be available.\n" +
            "Set to -1 to disable the HTTPS protocol.")
    public int https = 8081;

    @Setting(comment = "Set this path to your java key store if you don't want to\n" +
            "use the default self-signed one provided by the Web-API")
    public String customKeyStore = null;
    @Setting
    public String customKeyStorePassword = null;
    @Setting
    public String customKeyStoreManagerPassword = null;

    @Setting(comment = "Automatically report errors (your server IP is NOT collected,\n" +
            "neither any personal information). This just helps finding bugs.")
    public boolean reportErrors = true;


    @ConfigSerializable
    public static class APServer {
        @Setting(comment = "The display name of the server")
        public String name = "Localhost";
        @Setting(comment = "The Web-API URL for the server")
        public String apiUrl = "http://localhost:8080";

        private APServer() {}
    }
    @ConfigSerializable
    public static class APConfig {
        @Setting(comment = "The base path where the AdminPanel is served")
        public String basePath = "/admin/";
        @Setting(comment = "The list of servers in this AdminPanel")
        public List<APServer> servers;

        private APConfig() {
            servers = new ArrayList<>();
            servers.add(new APServer());
        }
    }
}
