package valandur.webapi.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ConfigSerializable
public class MainConfig extends BaseConfig implements IMainConfig {

    @Setting(comment = "Set this to false to completely disable the admin panel")
    public boolean adminPanel = true;
    @Override
    public boolean adminPanelEnabled() {
        return adminPanel;
    }

    @Setting(comment = "Change AdminPanel settings")
    public APConfig adminPanelConfig = new APConfig();
    @Override
    public IAdminPanelConfig getAdminPanelConfig() {
        return adminPanelConfig;
    }

    @Setting(comment = "Set this to true when working on the WebAPI. \n" +
            "This is NOT detailed debug info, so don't turn on if not \n" +
            "running the Web-API from an IDE")
    public boolean devMode = false;
    @Override
    public boolean isDevMode() {
        return devMode;
    }

    @Setting(comment = "This is the host the API will be listening on.\n" +
            "Default is \"localhost\" to prevent any access from outside\n" +
            "the server, but you can set this to \"0.0.0.0\" to listen\n" +
            "on all addresses IF YOU HAVE CONFIGURED THE PERMISSIONS")
    public String host = "localhost";
    @Override
    public String getHost() {
        return host;
    }

    @Setting(comment = "This tells the API on which port to listen for HTTP requests.\n" +
            "It is recommended to use a port above 1024, as those below\n" +
            "are reserved for the system and might not be available.\n" +
            "Set to -1 to disable the HTTP protocol.")
    public int http = 8080;
    @Override
    public int getHttpPort() {
        return http;
    }

    @Setting(comment = "This tells the API on which port to listen for HTTPS requests.\n" +
            "It is recommended to use a port above 1024, as those below\n" +
            "are reserved for the system and might not be available.\n" +
            "Set to -1 to disable the HTTPS protocol.")
    public int https = 8081;
    @Override
    public int getHttpsPort() {
        return https;
    }

    @Setting(comment = "Set this path to your java key store if you don't want to\n" +
            "use the default self-signed one provided by the Web-API")
    public String customKeyStore = null;
    @Override
    public String getCustomKeyStore() {
        return customKeyStore;
    }

    @Setting
    public String customKeyStorePassword = null;
    @Override
    public String getCustomKeyStorePassword() {
        return customKeyStorePassword;
    }

    @Setting
    public String customKeyStoreManagerPassword = null;
    @Override
    public String getCustomKeyStoreManagerPassword() {
        return customKeyStoreManagerPassword;
    }

    @Setting(comment = "Automatically report errors (your server IP is NOT collected,\n" +
            "neither any personal information). This just helps finding bugs.")
    public boolean reportErrors = true;
    @Override
    public boolean isReportingErrors() {
        return reportErrors;
    }

    @ConfigSerializable
    public static class APServer implements IAdminPanelServer {
        @Setting(comment = "The display name of the server")
        public String name = "Localhost";
        @Override
        public String getName() {
            return name;
        }

        @Setting(comment = "The Web-API URL for the server")
        public String apiUrl = "";
        @Override
        public String getApiUrl() {
            return apiUrl;
        }

        private APServer() {}
    }

    @ConfigSerializable
    public static class APConfig implements IAdminPanelConfig {
        @Setting(comment = "The base path where the AdminPanel is served")
        public String basePath = "/admin/";
        @Override
        public String getBasePath() {
            return basePath;
        }

        @Setting(comment = "The list of servers in this AdminPanel")
        public List<APServer> servers;
        @Override
        public List<IAdminPanelServer> getServers() {
            return servers.stream().map(s -> (IAdminPanelServer)s).collect(Collectors.toList());
        }

        private APConfig() {
            servers = new ArrayList<>();
            servers.add(new APServer());
        }
    }
}
