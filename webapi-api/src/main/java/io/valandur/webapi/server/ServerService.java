package io.valandur.webapi.server;

import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;
import io.valandur.webapi.info.ServerInfo;

public abstract class ServerService<T extends WebAPI<?, ?>> extends Service<T> {

  public ServerService(T webapi) {
    super(webapi);
  }

  public abstract ServerInfo getInfo();
}
