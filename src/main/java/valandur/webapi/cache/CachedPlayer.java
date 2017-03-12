package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.json.JsonConverter;

import java.util.*;

public class CachedPlayer extends CachedEntity {
    @JsonProperty
    public String name;

    public String address;
    public Integer latency;

    public static CachedPlayer copyFrom(Player player) {
        return copyFrom(player, false);
    }
    public static CachedPlayer copyFrom(Player player, boolean details) {
        CachedPlayer cache = new CachedPlayer();
        cache.name = player.getName();
        cache.uuid = player.getUniqueId().toString();
        cache.location = CachedLocation.copyFrom(player.getLocation());

        if (details) {
            cache.details = true;
            cache.velocity = player.getVelocity().clone();
            cache.rotation = player.getRotation().clone();
            cache.address = player.getConnection().getAddress().toString();
            cache.latency = player.getConnection().getLatency();
            cache.properties = JsonConverter.toJson(player.getApplicableProperties(), true);
            cache.data = JsonConverter.toJson(player.toContainer(), true);
        }
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return CacheConfig.durationPlayer;
    }
    @Override
    public Optional<Object> getLive() {
        Optional<Player> p = Sponge.getServer().getPlayer(UUID.fromString(uuid));
        if (!p.isPresent())
            return Optional.empty();
        return Optional.of(p.get());
    }

    @Override
    @JsonProperty
    public String getLink() {
        return "/api/player/" + uuid;
    }
}
