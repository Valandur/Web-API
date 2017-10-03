package valandur.webapi.api.message;

import org.spongepowered.api.text.Text;

import java.util.List;
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
     * Gets all the targets this message is sent to, in case it is sent to multiple players.
     * @return The list of target uuids.
     */
    List<UUID> getTargets();

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
    List<IMessageOption> getOptions();

    /**
     * True if the message object has options attached, false otherwise.
     * @return True if the message object has options, false otherwise.
     */
    boolean hasOptions();

    /**
     * Specifies wether the targets can reply multiple times or just once to this message. (This is per target)
     * @return True if the targets can only reply once, false otherwise.
     */
    Boolean isOnce();
}
