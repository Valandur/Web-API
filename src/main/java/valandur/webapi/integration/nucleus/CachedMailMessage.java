package valandur.webapi.integration.nucleus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.nucleuspowered.nucleus.api.nucleusdata.MailMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.player.CachedPlayer;

import java.time.Instant;

@ApiModel("NucleusMailMessage")
public class CachedMailMessage  extends CachedObject<MailMessage> {

    @ApiModelProperty(value = "The instant when the message was sent", required = true)
    private Instant date;
    public Instant getDate() {
        return date;
    }

    @ApiModelProperty(value = "The message content", required = true)
    private String message;
    public String getMessage() {
        return message;
    }

    @ApiModelProperty(value = "The sender of the message")
    private CachedPlayer sender;
    public CachedPlayer getSender() {
        return sender;
    }


    public CachedMailMessage(MailMessage value) {
        super(value);

        this.date = Instant.ofEpochMilli(value.getDate().toEpochMilli());
        this.message = value.getMessage();
        this.sender = cacheService.getPlayer(value.getSender().orElse(null));
    }

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getLink() {
        return null;
    }
}
