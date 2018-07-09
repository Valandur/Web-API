package valandur.webapi.integration.nucleus;

import io.github.nucleuspowered.nucleus.api.nucleusdata.MailMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedInstant;
import valandur.webapi.cache.player.CachedPlayer;

@ApiModel("NucleusMailMessage")
public class CachedMailMessage  extends CachedObject<MailMessage> {

    @ApiModelProperty(value = "The instant when the message was sent", required = true)
    private CachedInstant date;
    public CachedInstant getDate() {
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

        this.date = new CachedInstant(value.getDate());
        this.message = value.getMessage();
        this.sender = cacheService.getPlayer(value.getSender().orElse(null));
    }
}
