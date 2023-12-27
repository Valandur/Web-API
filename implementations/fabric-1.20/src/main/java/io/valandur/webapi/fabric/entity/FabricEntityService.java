package io.valandur.webapi.fabric.entity;

import io.valandur.webapi.entity.Entity;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.fabric.FabricWebAPI;
import java.util.Collection;

public class FabricEntityService extends EntityService<FabricWebAPI> {

  public FabricEntityService(FabricWebAPI webapi) {
    super(webapi);
  }

  @Override
  public Collection<Entity> getEntities() {
    return null;
  }
}
