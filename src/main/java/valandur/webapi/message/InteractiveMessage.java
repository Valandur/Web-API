package valandur.webapi.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents an interactive message which can be sent to players.
 */
@ApiModel("InteractiveMessage")
public class InteractiveMessage extends CachedObject<InteractiveMessage> {

    private UUID uuid;
    /**
     * Gets the unique id of the message. This is generated when the messages is created.
     * @return The unique id of the message.
     */
    @ApiModelProperty(value = "The unique UUID of this message", required = true)
    public UUID getUUID() {
        return uuid;
    }

    private String id;
    /**
     * The id of the message. Useful to identify it in the response.
     * @return The id of the message.
     */
    @ApiModelProperty(
            value = "The id of the message. Used for sender of the message to identify responses.",
            required = true)
    public String getId() {
        return id;
    }

    private String target;
    /**
     * The target of the message, usually a player uuid. Can be set to "server" to send to all online players.
     * @return The uuid of the target, or "server" to send to all online players.
     */
    @ApiModelProperty(value = "The target of the message, usually this is a player UUID. " +
            "Can be set to \"server\" to send to all online players.", required = true)
    @JsonDetails
    public String getTarget() {
        return target;
    }

    private List<String> targets;
    /**
     * Gets all the targets this message is sent to, in case it is sent to multiple players.
     * @return The list of target UUIDs.
     */
    @ApiModelProperty("A list of targets that will receive the message. Usually a list of player UUIDs")
    @JsonDetails
    public List<String> getTargets() { return targets; }

    private String message;
    /**
     * The message that is sent.
     * @return The message content.
     */
    @ApiModelProperty(dataType = "string", value = "The actual content of the message")
    @JsonDetails
    public Text getMessage() {
        if (message == null) {
            return null;
        }
        return TextSerializers.FORMATTING_CODE.deserialize(message);
    }

    private Boolean once;
    /**
     * Specifies wether the targets can reply multiple times or just once to this message. (This is per target)
     * @return True if the targets can only reply once, false otherwise.
     */
    @ApiModelProperty("True if this message can only be replied to once per target, false otherwise")
    @JsonDetails
    public Boolean isOnce() {
        return once;
    }

    private List<InteractiveMessageOption> options;
    /**
     * A map of answer keys to values. The value is displayed to the player as a clickable option, and upon clicking
     * it the key is sent to the message web hook.
     * @return A map of option keys to values.
     */
    @ApiModelProperty("Clickable options that the player can select from")
    @JsonDetails
    public List<InteractiveMessageOption> getOptions() {
        return new ArrayList<>(options);
    }

    /**
     * True if the message object has options attached, false otherwise.
     * @return True if the message object has options, false otherwise.
     */
    @JsonIgnore
    public boolean hasOptions() {
        return options != null;
    }




    public InteractiveMessage() {
        super(null);

        this.uuid = UUID.randomUUID();
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/message/" + uuid;
    }

    @Override
    public Optional<InteractiveMessage> getLive() {
        return super.getLive();
    }
}
