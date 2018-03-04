package valandur.webapi.api.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.cache.ICachedObject;

import java.util.List;
import java.util.UUID;

/**
 * Represents an interactive message which can be sent to players.
 */
@ApiModel("InteractiveMessage")
public interface IInteractiveMessage extends ICachedObject<IInteractiveMessage> {

    /**
     * Gets the unique id of the message. This is generated when the messages is created.
     * @return The unique id of the message.
     */
    @ApiModelProperty(value = "The unique UUID of this message", required = true)
    UUID getUUID();

    /**
     * The id of the message. Useful to identify it in the response.
     * @return The id of the message.
     */
    @ApiModelProperty(
            value = "The id of the message. Used for sender of the message to identify responses.",
            required = true)
    String getId();

    /**
     * The target of the message, usually a player uuid.
     * @return The uuid of the target.
     */
    @ApiModelProperty(value = "The target of the message, usually this is a player UUID", required = true)
    UUID getTarget();

    /**
     * Gets all the targets this message is sent to, in case it is sent to multiple players.
     * @return The list of target uuids.
     */
    @ApiModelProperty("A list of targets that will receive the message. Usually a list of player UUIDs")
    List<UUID> getTargets();

    /**
     * The message that is sent.
     * @return The message content.
     */
    @ApiModelProperty(dataType = "string", value = "The actual content of the message")
    Text getMessage();

    /**
     * A map of answer keys to values. The value is displayed to the player as a clickable option, and upon clicking
     * it the key is sent to the message web hook.
     * @return A map of option keys to values.
     */
    @ApiModelProperty("Clickable options that the player can select from")
    List<IInteractiveMessageOption> getOptions();

    /**
     * True if the message object has options attached, false otherwise.
     * @return True if the message object has options, false otherwise.
     */
    boolean hasOptions();

    /**
     * Specifies wether the targets can reply multiple times or just once to this message. (This is per target)
     * @return True if the targets can only reply once, false otherwise.
     */
    @ApiModelProperty("True if this message can only be replied to once per target, false otherwise")
    Boolean isOnce();
}
