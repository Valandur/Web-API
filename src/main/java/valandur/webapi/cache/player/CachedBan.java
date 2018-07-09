package valandur.webapi.cache.player;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.util.ban.Ban;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCommandSource;
import valandur.webapi.cache.misc.CachedInstant;
import valandur.webapi.cache.misc.CachedText;

@ApiModel("Ban")
public class CachedBan extends CachedObject<Ban> {

    @ApiModelProperty(value = "The moment when this ban was created", required = true)
    public CachedInstant createdOn;

    @ApiModelProperty("The date when this ban expires")
    public CachedInstant expiresOn;

    @ApiModelProperty("The reason why the player was banned")
    public CachedText reason;

    @ApiModelProperty("The source that issued the ban")
    public CachedText banSource;

    @ApiModelProperty("The command source associated with the ban")
    public CachedCommandSource commandSource;


    public CachedBan(Ban value) {
        super(value);

        this.createdOn = new CachedInstant(value.getCreationDate());
        this.expiresOn = value.getExpirationDate().map(CachedInstant::new).orElse(null);
        this.reason = value.getReason().map(CachedText::new).orElse(null);
        this.banSource = value.getBanSource().map(CachedText::new).orElse(null);
        this.commandSource = value.getBanCommandSource().map(CachedCommandSource::new).orElse(null);
    }
}
