package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

public class CachedPlayer extends CachedEntity {
    @JsonProperty
    public String name;

    public String address;
    public Integer latency;


    public CachedPlayer(Player player) {
        super(player);

        this.name = player.getName();
        this.address = player.getConnection().getAddress().toString();
        this.latency = player.getConnection().getLatency();
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
