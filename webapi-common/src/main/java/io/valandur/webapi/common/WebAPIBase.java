package io.valandur.webapi.common;

import io.valandur.webapi.WebAPI;
import io.valandur.webapi.common.entity.EntityServlet;
import io.valandur.webapi.common.player.PlayerServlet;
import io.valandur.webapi.common.security.SecurityService;
import io.valandur.webapi.common.server.ServerServlet;
import io.valandur.webapi.common.web.BaseServlet;
import io.valandur.webapi.common.web.WebServer;
import io.valandur.webapi.common.world.WorldServlet;
import io.valandur.webapi.config.SecurityConfig;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class WebAPIBase<T extends WebAPIBase<T, Conf>, Conf extends SecurityConfig> extends
    WebAPI<T, Conf> {

  public static WebAPIBase<?, ?> getInstance() {
    return (WebAPIBase<?, ?>) WebAPI.instance;
  }

  protected Set<BaseServlet> servlets = new HashSet<>();
  public Set<BaseServlet> getServlets() {
    return this.servlets;
  }

  protected WebServer webServer;

  protected SecurityService securityService;

  public SecurityService getSecurityService() {
    return securityService;
  }

  public void load() {
    super.load();

    servlets = new HashSet<>();
    servlets.add(new WorldServlet());
    servlets.add(new PlayerServlet());
    servlets.add(new EntityServlet());
    servlets.add(new ServerServlet());

    securityService = new SecurityService(this);
    webServer = new WebServer(this);
  }

  public void start() {
    webServer.start();
  }

  public void stop() {
    webServer.stop();
  }
}
