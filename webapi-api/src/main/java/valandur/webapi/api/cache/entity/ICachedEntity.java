package valandur.webapi.api.cache.entity;

import org.spongepowered.api.entity.Entity;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.misc.ICachedCatalogType;

import java.util.UUID;

public interface ICachedEntity extends ICachedObject<Entity> {

    ICachedCatalogType getType();

    UUID getUUID();
}
