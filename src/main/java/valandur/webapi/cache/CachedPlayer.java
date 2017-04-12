package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import valandur.webapi.json.JsonConverter;

import java.util.*;

public class CachedPlayer extends CachedEntity {
    @JsonProperty
    public String name;

    public JsonNode achievements;
    public JsonNode address;
    public JsonNode armour;
    public JsonNode food;
    public JsonNode gameMode;
    public JsonNode inventory;
    public JsonNode latency;
    public JsonNode profile;


    public CachedPlayer(Player player) {
        super(player);

        this.name = player.getName();

        this.achievements = JsonConverter.toJson(player.getAchievementData().achievements());
        this.address = JsonConverter.toJson(player.getConnection().getAddress().toString());
        this.armour = this.getArmour(player);
        this.food = JsonConverter.toJson(player.getFoodData());
        this.gameMode = JsonConverter.toJson(player.gameMode().get().getId());
        this.inventory = JsonConverter.toJson(player.getInventory());
        this.latency = JsonConverter.toJson(player.getConnection().getLatency());
        this.profile = JsonConverter.toJson(player.getProfile().getPropertyMap());
    }
    private JsonNode getArmour(Player player) {
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        obj.set("helmet", JsonConverter.toJson(player.getHelmet().map(i -> i.getItem().getId()).orElse(null)));
        obj.set("chestplate", JsonConverter.toJson(player.getChestplate().map(i -> i.getItem().getId()).orElse(null)));
        obj.set("leggings", JsonConverter.toJson(player.getLeggings().map(i -> i.getItem().getId()).orElse(null)));
        obj.set("boots", JsonConverter.toJson(player.getBoots().map(i -> i.getItem().getId()).orElse(null)));
        return obj;
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
