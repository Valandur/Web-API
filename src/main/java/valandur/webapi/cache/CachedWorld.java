package valandur.webapi.cache;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;
import valandur.webapi.misc.JsonConverter;

import java.util.Optional;
import java.util.UUID;

public class CachedWorld extends CachedObject {

    @Expose
    public String name;

    @Expose
    public String uuid;

    public JsonElement data;

    public static CachedWorld copyFrom(World world) {
        return copyFrom(world, false);
    }
    public static CachedWorld copyFrom(World world, boolean details) {
        CachedWorld cache = new CachedWorld();
        cache.name = world.getName();
        cache.uuid = world.getUniqueId().toString();
        if (details) {
            cache.details = true;
            cache.data = JsonConverter.containerToJson(world.getProperties().toContainer());
        }
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return CacheConfig.world;
    }
    @Override
    public Optional<Object> getLive() {
        Optional<World> w = Sponge.getServer().getWorld(UUID.fromString(uuid));
        if (!w.isPresent())
            return Optional.empty();
        return Optional.of(w.get());
    }
}
