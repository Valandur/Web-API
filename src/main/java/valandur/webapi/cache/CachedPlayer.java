package valandur.webapi.cache;

import com.google.gson.annotations.Expose;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

public class CachedPlayer {
    @Expose
    public String name;

    @Expose
    public String uuid;

    public CachedLocation location;
    public CachedVector3d velocity;
    public CachedVector3d rotation;
    public String address;
    public int latency;
    public Map<String, Object> data;
    public Map<String, Object> properties;

    public static CachedPlayer copyFrom(Player player) {
        return copyFrom(player, false);
    }
    public static CachedPlayer copyFrom(Player player, boolean details) {
        CachedPlayer cache = new CachedPlayer();
        cache.name = player.getName();
        cache.uuid = player.getUniqueId().toString();
        if (details) {
            cache.location = CachedLocation.copyFrom(player.getLocation());
            cache.velocity = CachedVector3d.copyFrom(player.getVelocity());
            cache.rotation = CachedVector3d.copyFrom(player.getRotation());
            cache.address = player.getConnection().getAddress().toString();
            cache.latency = player.getConnection().getLatency();
            cache.data = DataCache.containerToMap(player);
            cache.properties = DataCache.propertiesToMap(player);
        }
        return cache;
    }
}
