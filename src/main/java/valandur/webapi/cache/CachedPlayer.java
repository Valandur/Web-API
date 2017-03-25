package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.json.JsonConverter;

import java.util.*;

public class CachedPlayer extends CachedEntity {
    @JsonProperty
    public String name;

    public String address;
    public Integer latency;
    public JsonNode achievements;
    public JsonNode profile;


    public CachedPlayer(Player player) {
        super(player);

        this.name = player.getName();
        this.address = player.getConnection().getAddress().toString();
        this.latency = player.getConnection().getLatency();
        this.achievements = JsonConverter.toJson(player.getAchievementData().achievements());
        this.profile = JsonConverter.toJson(player.getProfile().getPropertyMap());
    }

    @Override
    public int getCacheDuration() {
        return CacheConfig.durationPlayer;
    }
    @Override
    public Optional<?> getLive() {
        return Sponge.getServer().getPlayer(UUID.fromString(uuid));
    }

    @Override
    @JsonProperty
    public String getLink() {
        return "/api/player/" + uuid;
    }
}
