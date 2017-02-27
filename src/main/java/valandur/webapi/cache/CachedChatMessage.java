package valandur.webapi.cache;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Date;
import java.util.Optional;

public class CachedChatMessage extends CachedObject {

    public Long date;
    public CachedPlayer sender;
    public String message;

    public static CachedChatMessage copyFrom(Date date, Player sender, Text message) {
        CachedChatMessage msg = new CachedChatMessage();
        msg.date = date.toInstant().getEpochSecond();
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
