package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.json.JsonConverter;

import java.util.Optional;

public class CachedLocation extends CachedObject {

    @JsonIgnore
    private CachedWorld _world;

    @JsonProperty
    public JsonNode world;

    @JsonProperty
    public Vector3d position;


    public CachedLocation(Location<World> location) {
        this._world = DataCache.getWorld(location.getExtent());
        this.world = JsonConverter.toJson(this._world);
        this.position = location.getPosition().clone();
    }

    @Override
    public Optional<?> getLive() {
        Optional<?> w = _world.getLive();
        return w.map(o -> new Location<>((World) o, position));
    }

    @JsonIgnore
    public String getWorldUUID() {
        return _world.uuid;
    }
}
