package io.valandur.webapi;

import io.valandur.webapi.config.Config;
import io.valandur.webapi.web.WebServer;

public abstract class WebAPIBase<T extends WebAPIBase<T, Conf>, Conf extends Config> extends WebAPI<T, Conf> {

    protected WebServer webServer;

    public void load() {
        super.load();

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
