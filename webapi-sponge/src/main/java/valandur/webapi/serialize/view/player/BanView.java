package valandur.webapi.serialize.view.player;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ban.Ban;
import valandur.webapi.serialize.BaseView;

import java.time.Instant;

@ApiModel("Ban")
public class BanView extends BaseView<Ban> {

    @ApiModelProperty(value = "The moment when this ban was created", required = true)
    public Instant createdOn;

    @ApiModelProperty("The date when this ban expires")
    public Instant expiresOn;

    @ApiModelProperty("The reason why the player was banned")
    public Text reason;

    @ApiModelProperty("The source that issued the ban")
    public Text banSource;

    @ApiModelProperty("The command source associated with the ban")
    public CommandSource commandSource;


    public BanView(Ban value) {
        super(value);

        this.createdOn = value.getCreationDate();
        this.expiresOn = value.getExpirationDate().orElse(null);
        this.reason = value.getReason().orElse(null);
        this.banSource = value.getBanSource().orElse(null);
        this.commandSource = value.getBanCommandSource().orElse(null);
    }
}
