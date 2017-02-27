package valandur.webapi.cache;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.misc.JsonConverter;

import java.util.*;

public class CachedPlayer extends CachedObject {
    @Expose
    public String name;

    @Expose
    public String uuid;

    public CachedLocation location;
    public CachedVector3d velocity;
    public CachedVector3d rotation;
    public String address;
    public Integer latency;
    public JsonElement data;
    public JsonElement properties;

    public static CachedPlayer copyFrom(Player player) {
        return copyFrom(player, false);
    }
    public static CachedPlayer copyFrom(Player player, boolean details) {
        CachedPlayer cache = new CachedPlayer();
        cache.name = player.getName();
        cache.uuid = player.getUniqueId().toString();
        if (details) {
            cache.details = true;
            cache.location = CachedLocation.copyFrom(player.getLocation());
            cache.velocity = CachedVector3d.copyFrom(player.getVelocity());
            cache.rotation = CachedVector3d.copyFrom(player.getRotation());
            cache.address = player.getConnection().getAddress().toString();
            cache.latency = player.getConnection().getLatency();
            cache.data = JsonConverter.containerToJson(player);
            cache.properties = JsonConverter.propertiesToJson(player);
        }
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return CacheConfig.player;
    }
    @Override
    public Optional<Object> getLive() {
        Optional<Player> p = Sponge.getServer().getPlayer(UUID.fromString(uuid));
        if (!p.isPresent())
            return Optional.empty();
        return Optional.of(p.get());
    }
}
