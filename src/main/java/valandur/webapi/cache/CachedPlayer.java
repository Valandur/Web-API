package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.json.JsonConverter;

import java.util.*;

public class CachedPlayer extends CachedEntity {
    @JsonProperty
    public String name;

    public JsonNode armour;
    public JsonNode connection;
    public JsonNode inventory;


    public CachedPlayer(Player player) {
        super(player);

        this.name = player.getName();

        this.armour = this.getArmour(player);
        this.connection = JsonConverter.toJson(player.getConnection());
        this.inventory = JsonConverter.toJson(player.getInventory());
    }
    private JsonNode getArmour(Player player) {
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        obj.set("helmet", JsonConverter.toJson(player.getHelmet().orElse(null)));
        obj.set("chestplate", JsonConverter.toJson(player.getChestplate().orElse(null)));
        obj.set("leggings", JsonConverter.toJson(player.getLeggings().orElse(null)));
        obj.set("boots", JsonConverter.toJson(player.getBoots().orElse(null)));
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
