package io.valandur.webapi.forge.entity;

import io.valandur.webapi.entity.Entity;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.forge.ForgeWebAPI;

import java.util.Collection;
import java.util.Collections;

public class ForgeEntityService extends EntityService<ForgeWebAPI> {

    public ForgeEntityService(ForgeWebAPI webapi) {
        super(webapi);
    }

    @Override
    public Collection<Entity> getEntities() {
        return Collections.emptySet();
    }
}
