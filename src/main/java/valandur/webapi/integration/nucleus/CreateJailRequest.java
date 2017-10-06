package valandur.webapi.integration.nucleus;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flowpowered.math.vector.Vector3d;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.util.Optional;

@JsonDeserialize
public class CreateJailRequest {

    @JsonDeserialize
    private String name;
    public String getName() {
        return name;
    }

    @JsonDeserialize
    private String world;
    public Optional<ICachedWorld> getWorld() {
        return WebAPIAPI.getCacheService().flatMap(srv -> srv.getWorld(world));
    }

    @JsonDeserialize
    private Vector3d position;
    public Vector3d getPosition() {
        return position;
    }

    @JsonDeserialize
    private Vector3d rotation;
    public Vector3d getRotation() {
        return rotation != null ? rotation : Vector3d.ZERO;
    }
}
