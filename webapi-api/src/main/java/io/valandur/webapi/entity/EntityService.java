package io.valandur.webapi.entity;

import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;
import java.util.Collection;

public abstract class EntityService<T extends WebAPI<?>> extends Service<T> {

  public EntityService(T webapi) {
    super(webapi);
  }

  public abstract Collection<Entity> getEntities();
}
