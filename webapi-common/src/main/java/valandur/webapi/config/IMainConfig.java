package valandur.webapi.config;

import java.util.List;

public interface IMainConfig {
    boolean adminPanelEnabled();
    IAdminPanelConfig getAdminPanelConfig();
    boolean isDevMode();
    String getHost();
    int getHttpPort();
    int getHttpsPort();
    String getCustomKeyStore();
    String getCustomKeyStorePassword();
    String getCustomKeyStoreManagerPassword();
    boolean isReportingErrors();

    interface IAdminPanelServer {
        String getName();
        String getApiUrl();
    }

    interface IAdminPanelConfig {
        String getBasePath ();
        List<IAdminPanelServer> getServers();
    }
}
