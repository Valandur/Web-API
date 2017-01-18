package valandur.webapi.cache;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Date;
import java.util.Optional;

public class CachedChatMessage extends CachedObject {
    private Date date;
    public Date getDate() {
        return date;
    }

    private Player sender;
    public Player getSender() {
        return sender;
    }

    private Text message;
    public Text getMessage() {
        return message;
    }

    public CachedChatMessage(Date date, Player sender, Text message) {
        this.date = date;
        this.sender = sender;
        this.message = message;
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
