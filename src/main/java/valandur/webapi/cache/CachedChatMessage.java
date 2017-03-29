package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.message.MessageEvent;
import valandur.webapi.json.JsonConverter;

import java.util.Date;

public class CachedChatMessage extends CachedObject {

    @JsonProperty
    public Long timestamp;

    @JsonProperty
    public JsonNode sender;

    @JsonProperty
    public String message;

    @JsonProperty
    public boolean wasCancelled;


    public CachedChatMessage(Player sender, MessageEvent event) {
        this.timestamp = (new Date()).toInstant().getEpochSecond();
        this.sender = JsonConverter.toJson(DataCache.getPlayer(sender.getUniqueId()).orElse(null));
        this.message = event.getMessage().toPlain();
        this.wasCancelled = event.isMessageCancelled();
    }
}
