package valandur.webapi.api.message;

import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.UUID;

/**
 * Represents an interactive message which can be sent to players.
 */
public interface IMessage {

    /**
     * The id of the message. Useful to identify it in the response.
     * @return The id of the message.
     */
    String getId();

    /**
     * The target of the message, usually a player uuid.
     * @return The uuid of the target.
     */
    UUID getTarget();

    /**
     * The message that is sent.
     * @return The message content.
     */
    Text getMessage();

    /**
     * A map of answer keys to values. The value is displayed to the player as a clickable option, and upon clicking
     * it the key is sent to the message web hook.
     * @return A map of option keys to values.
     */
    Map<String, String> getOptions();
}
