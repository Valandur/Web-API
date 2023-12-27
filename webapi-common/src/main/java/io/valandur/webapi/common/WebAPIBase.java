package io.valandur.webapi.common;

import io.valandur.webapi.WebAPI;
import io.valandur.webapi.common.chat.ChatServlet;
import io.valandur.webapi.common.entity.EntityServlet;
import io.valandur.webapi.common.info.InfoServlet;
import io.valandur.webapi.common.player.PlayerServlet;
import io.valandur.webapi.common.security.SecurityService;
import io.valandur.webapi.common.web.BaseServlet;
import io.valandur.webapi.common.web.WebServer;
import io.valandur.webapi.common.world.WorldServlet;
import java.util.HashSet;
import java.util.Set;

public abstract class WebAPIBase<T extends WebAPIBase<T, U>, U> extends WebAPI<T> {

  public static WebAPIBase<?, ?> getInstance() {
    return (WebAPIBase<?, ?>) WebAPI.instance;
  }

  protected final U plugin;
  public U getPlugin() {
    return plugin;
  }

  protected Set<BaseServlet> servlets;
  public Set<BaseServlet> getServlets() {
    return this.servlets;
  }

  protected WebServer webServer;

  protected SecurityService securityService;
  public SecurityService getSecurityService() {
    return securityService;
  }

  public WebAPIBase(U plugin) {
    super();

    this.plugin = plugin;

    init();

    securityService = new SecurityService(this);

    servlets = new HashSet<>();
    servlets.add(new WorldServlet());
    servlets.add(new PlayerServlet());
    servlets.add(new EntityServlet());
    servlets.add(new InfoServlet());
    servlets.add(new ChatServlet());

    webServer = new WebServer(this);
  }

  public void start() {
    webServer.start();
  }

  public void stop() {
    webServer.stop();
  }
}
