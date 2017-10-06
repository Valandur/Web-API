package valandur.webapi.api.cache.entity;

import org.spongepowered.api.entity.Entity;
import valandur.webapi.api.cache.ICachedObject;

import java.util.UUID;

public interface ICachedEntity extends ICachedObject<Entity> {

    String getType();

    UUID getUUID();
}
