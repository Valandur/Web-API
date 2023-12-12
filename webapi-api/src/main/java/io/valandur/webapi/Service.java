package io.valandur.webapi;

import io.valandur.webapi.logger.Logger;

public abstract class Service<T extends WebAPI<?>> {

  protected T webapi;
  protected Logger logger;

  public Service(T webapi) {
    this.webapi = webapi;
    this.logger = webapi.getLogger();
  }
}
