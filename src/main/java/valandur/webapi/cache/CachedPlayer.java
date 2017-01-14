package valandur.webapi.cache;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
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
    public int latency;

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
            cache.raw = JsonConverter.toJson(player);
        }
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return CacheDurations.player;
    }
}
