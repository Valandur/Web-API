package valandur.webapi.serialize.view.player;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ban.Ban;
import valandur.webapi.api.serialize.BaseView;

import java.time.Instant;

public class BanView extends BaseView<Ban> {

    public Instant createdOn;
    public Instant expiresOn;
    public Text reason;
    public Text banSource;
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
