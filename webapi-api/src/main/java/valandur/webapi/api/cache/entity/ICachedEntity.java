package valandur.webapi.api.cache.entity;

import com.flowpowered.math.vector.Vector3d;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.misc.ICachedInventory;
import valandur.webapi.api.cache.misc.ICachedLocation;

import java.util.UUID;

public interface ICachedEntity extends ICachedObject {

    String getType();

    UUID getUUID();

    ICachedLocation getLocation();

    Vector3d getRotation();

    Vector3d getVelocity();

    Vector3d getScale();

    ICachedInventory getInventory();
}
