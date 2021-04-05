package io.valandur.webapi;

import io.valandur.webapi.config.Config;
import io.valandur.webapi.security.SecurityService;
import io.valandur.webapi.web.WebServer;

public abstract class WebAPIBase<T extends WebAPIBase<T, Conf>, Conf extends Config> extends WebAPI<T, Conf> {

    public static WebAPIBase<?, ?> getInstance() {
        return (WebAPIBase<?, ?>) WebAPI.instance;
    }

    protected WebServer webServer;

    protected SecurityService securityService;

    public SecurityService getSecurityService() {
        return securityService;
    }

    public void load() {
        super.load();

        securityService = new SecurityService(this);

        webServer = new WebServer(this);
        webServer.load();
    }

    public void start() {
        webServer.start();
    }

    public void stop() {
        webServer.stop();
    }
}
