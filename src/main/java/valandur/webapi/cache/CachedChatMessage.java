package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Date;
import java.util.Optional;

public class CachedChatMessage extends CachedObject {

    @JsonProperty
    public Long timestamp;

    @JsonProperty
    public CachedPlayer sender;

    @JsonProperty
    public String message;

    public static CachedChatMessage copyFrom(Player sender, Text message) {
        CachedChatMessage msg = new CachedChatMessage();
        msg.timestamp = (new Date()).toInstant().getEpochSecond();
        msg.sender = CachedPlayer.copyFrom(sender);
        msg.message = message.toPlain();
        return msg;
    }

    @Override
    public int getCacheDuration() {
        return 0;
    }
    @Override
    public Optional<Object> getLive() {
        return Optional.empty();
    }
}
